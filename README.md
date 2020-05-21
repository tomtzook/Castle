# Castle
![Maven Central](https://img.shields.io/maven-central/v/com.github.tomtzook/castle)
![Travis (.org) branch](https://img.shields.io/travis/tomtzook/Castle/master.svg)
![GitHub](https://img.shields.io/github/license/tomtzook/Castle.svg)

Got a utility you need? Don't be an idjit!

![Bobby Singer](resources/bobby.jpeg)

A Java toolbox library for performing all things. Need to extract data from a zip file? Or maybe a way to handle exception in a smarter way? ~~Bobby~~ __Castle__ is here for you.

### Thread-Safety Annotations

__Castle__ uses source-annotations to designate
the thread-safety of different classes, in the intention
of informing users whether or not a class can be safetly used
in multi-threaded environment.

There are several annotations:
- `@NotThreadSafe`: indicates that the class cannot be safely used in
a multi-threaded environment (across several threads), without proper
user locking. The reason for lack of thread-safety is not specified, and
changes across classes. This however does not mean the class is not actually thread-safe,
only that there is no guarantee made that all of its methods are.
- `@Stateless`: indicates that the class has no actual state, and each
method use is completely independent.
- `@Immutable`: indicates that the class posses a state, however
that state is immutable, and cannot be changed.
- `@ThreadSafe`: indicates that the class takes measures to ensure
thread-safety and thus can be used in multi-threaded environments without
the need for additional user actions. How this is achieved depends on
the class, and generally should not matter.

`@Stateless`, `@Immutable` and `@ThreadSafe` are all safe for use
across multiple threads, for different reasons. However, when using
`@NotThreadSafe`, ensure the class is used by the same thread only, or
that any access to the class is done with appropriate locking.