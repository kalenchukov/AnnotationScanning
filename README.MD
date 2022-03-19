# AnnotationScanner
Сканер аннотаций по пакетам

### Использование
```java
AnnotationScanning annotationScanner = new AnnotationScanner();
annotationScanner.addPackage("dev.kalenchukov.annotationscanner.test.packages.films");
annotationScanner.addPackage("dev.kalenchukov.annotationscanner.test.packages.musics");
List<Class<?>> annotatedClasses = annotationScanner.findAnnotatedClasses(MyAnnotation.class);

for (Class<?> objectClass : annotatedClasses)
{
    System.out.println(objectClass.getName() + ".class");
}

/*
Результат выполнения:
dev.kalenchukov.annotationscanner.test.packages.films.Film.class
dev.kalenchukov.annotationscanner.test.packages.films.Video.class
dev.kalenchukov.annotationscanner.test.packages.musics.rock.RockMusic.class
dev.kalenchukov.annotationscanner.test.packages.musics.Music.class
*/
```