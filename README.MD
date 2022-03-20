# AnnotationScanner
Сканер аннотаций по пакетам

### Использование
```java
AnnotationScanning annotationScanner = new AnnotationScanner();
annotationScanner.addPackage("dev.kalenchukov.annotationscanner.tests.packages.films");
annotationScanner.addPackage("dev.kalenchukov.annotationscanner.tests.packages.musics");
List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

for (Class<?> objectClass : annotatedClasses)
{
    System.out.println(objectClass.getName() + ".class");
}

/*
Результат выполнения:
dev.kalenchukov.annotationscanner.testss.packages.films.Film.class
dev.kalenchukov.annotationscanner.testss.packages.films.Video.class
dev.kalenchukov.annotationscanner.testss.packages.musics.rock.RockMusic.class
dev.kalenchukov.annotationscanner.testss.packages.musics.Music.class
*/
```