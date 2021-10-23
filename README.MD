# AnnotationScanner
Сканер аннотаций по пакетам

### Использование
```java
AnnotationScanner annotationScanner = new AnnotationScanner();
annotationScanner.addPackage("dev.kalenchukov.annotationscanner.annotatedclasses1");
annotationScanner.addPackage("dev.kalenchukov.annotationscanner.annotatedclasses2");
Set<Class<?>> annotatedClasses = annotationScanner.findAnnotation(MyAnnotation.class);

for (Class<?> object : annotatedClasses)
{
    System.out.println(object.getName());
}

/*
Результат выполнения:
dev.kalenchukov.annotationscanner.annotatedclasses1.ClassAnnotated2
dev.kalenchukov.annotationscanner.annotatedclasses1.ClassAnnotated1
dev.kalenchukov.annotationscanner.annotatedclasses2.ClassAnnotated3
*/
```