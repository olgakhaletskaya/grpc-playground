package com.example.testgrpc.service;

import com.example.testgrpc.proto.Employee;
import com.example.testgrpc.proto.InfoServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class InfoGrpcService extends InfoServiceGrpc.InfoServiceImplBase {
    @Override
    public void getEmployeeInfo(Empty request, StreamObserver<Employee> responseObserver) {
        responseObserver.onNext(Employee.newBuilder().setName("Bob").setEmail("test@gmail.com").setSalary(1000).build());
        responseObserver.onCompleted();
    }
}
