/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.examples.helloworld;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.logging.Logger;

import java.io.File;
import java.io.InputStreamReader; 
import java.io.BufferedReader;
import java.io.FileInputStream; 
import java.awt.Desktop;
import java.util.*;
import java.lang.Thread;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class HelloWorldServer {
  private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    server = ServerBuilder.forPort(port)
        .addService(new GreeterImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        HelloWorldServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }


  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final HelloWorldServer server = new HelloWorldServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {

      HelloReply reply;
      
      //perform exe,one way 
      logger.info("run program:" + "0724maoyan.exe"); 
      try {
        Runnable task = new Runnable(){

          @Override
          public void run() {
            try {
              Desktop.getDesktop().open(new File("maoyan\\dist\\0724maoyan.exe"));
              Thread.sleep(30000);
            }catch (Exception e) {
              e.printStackTrace();  
              System.err.println("program:" + "0724maoyan.exe" + "not exist");  
            }
          }
        };
        Thread t = new Thread(task);
        t.start();
        t.join();
      } catch (Exception e) {
        //TODO: handle exception
      }


      try { 
        String pathname = "result.txt";
        File filename = new File(pathname);
        //Create an input flow object
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename),"GBK");
        //Create an object that converts the contents of a file into a language that a computer can read 
        BufferedReader br = new BufferedReader(reader); 
        StringBuilder sb = new StringBuilder();
        String line = ""; 
        line = br.readLine(); 
        while (line != null) {
          //Read in one row at a time
          line = br.readLine();
          sb.append(line + "\n"); 
        }
        String str = sb.toString();
        // logger.info(str);
  
        reply = HelloReply.newBuilder().setMessage(str).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
      }catch (Exception e) { 
        e.printStackTrace(); 
      }
      
      // HelloReply reply = HelloReply.newBuilder().setMessage("https://baike.baidu.com/item/" + req.getName()).build();
      
    }
  }
}  