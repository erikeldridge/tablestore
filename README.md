# Generate protobuf Model class

Install: `brew install protobuf`

Generate: `protoc -I=app/src/main --java_out=app/src/main/java app/src/main/proto/queue.proto`
