# runtime-aop-starter

runtime-aop-starter provides executing any injected code into specified methods of your beans in Spring Boot Application in runtime mode

## Features

- Supports groovy scripts
- injects code before specified bean method
- injects code after specified bean method
- overrides bean method by injected code
- loads injecting code from several sources (file, jdbc, etc) in runtime

## Installation

Include into dependecies

```java
<dependency>
    <groupId>ru.promoit</groupId>
    <artifactId>runtime-aop-starter</artifactId>
</dependency>
```

Specify property (for example)
```
runtime-aop.config.aspect-map=before-org.example.component.TestComponent1#testMethod1=once-file:src/main/resources/BeforeAspect1.groovy;\
  after-org.example.component.TestComponent2#testMethod3=ever-jdbc:select code from table_groovy limit 1;\
  override-org.example.component.TestController#test=instant-file:src/main/resources/OverrideAspect.groovy
```

## Details
### Configuring
Property pattern
```java
runtime-aop.config.aspect-map={advice}-{class}#{method}={loadingMode}-{sourceType}:{sourceValue}
```
parameter _loadingMode_:

- _advice_ - should be before/after/override (loads script before/after method call or overrides it) 
- _instant_ - loads script on starting of apllication
- _once_ - loads script once on target method call
- _ever_ - loads scripts every time when target method calls

parameter _sourceType_:

- _file_ - loads script from file (path: _{sourceValue}_)
- _jdbc_ - loads script from SQL Database (sql: _{sourceValue}_)
- _class_ - loads script from classpath

Example:
```
runtime-aop.config.aspect-map=org.example.component.TestComponent1#testMethod1=once-file:src/main/resources/BeforeAspect1.groovy
```
_org.example.component.TestComponent1_ - target class
_testMethod1_ - target method
_once_ - loading mode
_file_ - source type
_src/main/resources/BeforeAspect1.groovy_ - source value

### Providing custom code source types
You can add cusom source types by defining bean. For example:
```java
@Component
public class GroovyAspectFileProvider implements GroovyAspectSourceProvider {
    @Override
    public boolean match(String driver) {
        return "http".equals(driver);
    }

    @Override
    public String provide(String property) throws Throwable {
        GetMethod get = new GetMethod("http://" + property);
        InputStream in = get.getResponseBodyAsStream();
        String code = new String(in.readAllBytes());
        get.releaseConnection();
        return code;
    }
}
```
```
runtime-aop.config.aspect-map=override-org.example.component.TestComponent1#testMethod1=once-http:someurl.com/code.groovy
```
New _sourceType_ (http) is provided in your application


### Inject Before method
For example, we need to inject code before bean method _testMethod1_
```java
@Component
public class TestComponent1 {
    public String testMethod1(String param) {
        return "test1: " + s;
    }
}
```

Specified groovy code
```groovy
import org.springframework.beans.factory.BeanFactory
import ru.promoit.aspect.BeforeAspect

class BeforeAspect1 implements BeforeAspect {
    Object[] beforeAdvice(Object obj, Object[] args, Object beanFactory) throws Throwable {
        String s = args[0] as String
        Object[] objs = new Objects[1]
        objs[0] = s.toUpperCase()
        return objs
    }
}
```
_beforeAdvice_ returns Object[] parameters for target method (TestComponent1#testMethod1)
Parameters:
 - _obj_ - target bean
 - _args_ - bean method's arguments
 - _beanFactory_ - Spring BeanFactory (for communicating with other bean in Spring Context)
(In case above inected groovy script changes _param_ argument of _testMethod1_ to UpperCase String)

Native method call example:
```java
testComponent1.testMethod1("hello") //returns "test1: hello"
```
Injected method with BeforeAspect1 call example:
```java
testComponent1.testMethod1("hello") //returns "test1: HELLO"
```

### Inject After method
For example, we need to inject code after bean method _testMethod1_
```java
@Component
public class TestComponent1 {
    public String testMethod1(String param) {
        return "test1: " + s;
    }
}
```
Specified groovy code
```groovy
import org.springframework.beans.factory.BeanFactory
import ru.promoit.aspect.AfterAdvice

class AfterAspect1 implements AfterAdvice {
    Object afterAdvice(Object obj, Object[] args, Object result, Object beanFactory) throws RuntimeException {
        return result.toUpperCase();
    }
}
```
_afterAdvice_ returns new result of target method (TestComponent1#testMethod1)
Parameters:
 - _obj_ - target bean
 - _args_ - bean method's arguments
 - _result_ - original result of target bean method
 - _beanFactory_ - Spring BeanFactory (for communicating with other bean in Spring Context)
(In case above inected groovy script changes _result_ of _testMethod1_ to UpperCase String)

Native method call example:
```java
testComponent1.testMethod1("hello") //returns "test1: hello"
```
Injected method with AfterAspect1 call example:
```java
testComponent1.testMethod1("hello") //returns "TEST1: HELLO"
```

### Inject Overrided method
For example, we need to inject code instead of bean method _testMethod1_
```java
@Component
public class TestComponent1 {
    public String testMethod1(String param) {
        return "test1: " + s;
    }
}
```
Specified groovy code
```groovy
import org.springframework.beans.factory.BeanFactory
import ru.promoit.aspect.OverrideAspect

class OverrideAspect1 implements OverrideAspect {
    Object overrideAdvice(Object obj, Object[] args, Object beanFactory) throws RuntimeException {
        return "123"
    }
}
```
_overrideAdvice_ returns new result of target method (TestComponent1#testMethod1)
Parameters:
 - _obj_ - target bean
 - _args_ - bean method's arguments
 - _beanFactory_ - Spring BeanFactory (for communicating with other bean in Spring Context)
(In case above inected groovy script changes _result_ of _testMethod1_ to "123")

Native method call example:
```java
testComponent1.testMethod1("hello") //returns "test1: hello"
```
Injected method with OverrideAspect1 call example:
```java
testComponent1.testMethod1("hello") //returns "123"
```

## Using javaagent
runtime-aop-starter also can be loaded as a javaagent. In this case it changes the bytecode of specified classes. It provides loading and running scripts much faster (as a native code).
runtime-aop-starter uses _net.bytebuddy.byte-buddy_ to change the bytecode.

runtime-aop-starter can be loaded as javaagent by specifying JVM option and _javaagent aspect property_ in the same format as it was below
```java
java -jar <your_app.jar> -javaagent:<path-to>\runtime-aop-starter.jar=before-org.example.component.TestComponent1#testMethod1=once-file:src/main/resources/BeforeAspect1.groovy;\
        after-org.example.component.TestComponent2#testMethod3=ever-jdbc:select code from table_groovy limit 1;\
        override-org.example.component.TestController#test=instant-file:src/main/resources/OverrideAspect.groovy
```
 And Include into dependecies

```java
<dependency>
    <groupId>ru.promoit</groupId>
    <artifactId>runtime-aop-starter</artifactId>
</dependency>
```

## Author
https://promo-z.ru/
