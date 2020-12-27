package com.castle.function;

public class Test {

    public static void main(String[] args) throws Exception {
        Pipeline<String, Exception> pipeline = Processor.identity(String.class, Exception.class)
                .andThen(Object::toString)
                .pipeTo(System.out::println)
                .divergeTo(System.err::println);

        pipeline.process("Hello");
    }
}
