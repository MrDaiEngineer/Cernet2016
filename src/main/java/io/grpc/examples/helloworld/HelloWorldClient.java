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

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.awt.Desktop;
import java.net.*;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class HelloWorldClient {
  private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());

  private final ManagedChannel channel;
  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
  public HelloWorldClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build());
  }

  /** Construct client for accessing HelloWorld server using the existing channel. */
  HelloWorldClient(ManagedChannel channel) {
    this.channel = channel;
    blockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Say hello to server. */
  public void greet(String name) {
    logger.info("Will try to search " + name + " ...");

    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;
    try {
      response = blockingStub.sayHello(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Searching content: " + name);

    //create a new file and write string to it.
    try {
      File file = new File("getResult.txt");
      file.createNewFile();
      boolean fvar = file.createNewFile();
      if (fvar){
        System.out.println("File has been created successfully");
      }else{
        System.out.println("File already present at the specified location");
      }
      //write string to file
      String sourceString = response.getMessage();
      byte[] sourceByte = sourceString.getBytes();
      FileOutputStream outStream = new FileOutputStream(file);
      outStream.write(sourceByte);
      outStream.close();
      //open the file
      // Desktop.getDesktop().open(file);
     }catch (IOException e) {
        System.out.println("Exception Occurred:");
        e.printStackTrace();
    }

    //open one site
    // String site = response.getMessage();
    // try {
    //   Desktop desktop = Desktop.getDesktop();
    //   if (desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
    //     URI uri = new URI(site);
    //     desktop.browse(uri);
    //   }
    // } catch (IOException ex) {
    //   System.out.println(ex);
    // } catch (URISyntaxException ex) {
    //   System.out.println(ex);
    // }
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {
    HelloWorldClient client = new HelloWorldClient("localhost", 50051);
    try {
      /* Access a service running on the local machine on port 50051 */
      Scanner scanner = new Scanner(System.in);
      System.out.print("scanner Name:");
      String name = scanner.next();

      // String user = "2";
      if (args.length > 0) {
        name = args[0]; /* Use the arg as the name to greet if provided */
      }
      client.greet(name);
    } finally {
      client.shutdown();
    }
  }
}
