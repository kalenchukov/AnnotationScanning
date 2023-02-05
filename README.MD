# Справка
## AnnotationScanning
Сканер аннотаций по пакетам

## Использование
```java
AnnotationScanning annotationScanner = new AnnotationScanner();
annotationScanner.addPackage("dev.kalenchukov.annotation.scanning.tests.packages.films");
annotationScanner.addPackage("dev.kalenchukov.annotation.scanning.tests.packages.musics");
List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

for (Class<?> objectClass : annotatedClasses)
{
    System.out.println(objectClass.getName() + ".class");
}
```

Результат выполнения:
```
Film.class
Video.class
RockMusic.class
Music.class
```
