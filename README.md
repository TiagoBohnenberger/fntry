# FnTry

![GitHub License](https://img.shields.io/github/license/tiagobohnenberger/fntry)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.tiagobohnenberger/fntry.svg)](https://central.sonatype.com/artifact/io.github.tiagobohnenberger/fntry)
[![javadoc](https://javadoc.io/badge2/io.github.tiagobohnenberger/fntry/javadoc.svg)](https://javadoc.io/doc/io.github.tiagobohnenberger/fntry)

## 📌 About the Project

**FnTry** is a Java library that facilitates functional handling of checked and unchecked exceptions. It allows developers to handle operations that may throw exceptions in a more elegant and concise manner, promoting cleaner and more expressive code.

## 🚀 Features

- **Functional exception handling**: Use lambda functions to capture and manage exceptions without the need for verbose try-catch blocks.
- **Compatibility with checked and unchecked exceptions**: Handle both types of exceptions uniformly.

Transform a code like this:
```java
try {
    someString = someString.trim();
} catch (NullPointerException e) {
    someString = "";
}
            
try {
    someOtherString = someOtherString.trim();
} catch (NullPointerException e) {
    someOtherString = "";
}
```

into code like this:
```java
someString = Try.of(someString::trim).orElse("");
someOtherString = Try.of(someOtherString::trim).orElse("")
```

Or try to close some external resource, and if it's not possible, log the error:
```java
Try.just(bufferedReader::close)
    .otherwise(ex -> log.error("Error on auto closing", ex));
```

The possibilities are many, and we often find ourselves in situations where we need to execute
operations that should not stop the application flow, but still require some handling.
FnTry was created to solve this problem.

## 📦 Installation

To include **FnTry** in your project, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.tiagobohnenberger</groupId>
    <artifactId>fntry</artifactId>
    <version>1.0.0</version>
</dependency>
```
Java 8 or later is required to use this library.