package com.example.testgrpc.service;


import com.example.testgrpc.proto.HelloReply;
import com.example.testgrpc.proto.HelloRequest;
import com.example.testgrpc.proto.Profile;
import com.example.testgrpc.proto.SimpleGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.concurrent.TimeUnit;

@GrpcService
public class HelloServiceImpl extends SimpleGrpc.SimpleImplBase {

//    mvn spring-boot:run
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        String greeting = "Hello, " + request.getName() + "!";
        HelloReply response = HelloReply.newBuilder().setMessage(greeting).build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void register(Profile request, StreamObserver<Profile> responseObserver) {
        String greeting = "Hello, " + request.getName() + "!";
        HelloReply response = HelloReply.newBuilder().setMessage(greeting).build();

        // Send the response back to the client
        try {
            responseObserver.onNext(request);
            TimeUnit.SECONDS.sleep(1);
            responseObserver.onNext(request);
            TimeUnit.SECONDS.sleep(1);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onCompleted();
        }
    }
}