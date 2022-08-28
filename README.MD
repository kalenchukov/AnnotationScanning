# AnnotationScanning

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
/*
Результат выполнения:
Film.class
Video.class
RockMusic.class
Music.class
*/
```

## Установка

Для установки этого проекта, введите в терминале:

```bash
git clone https://github.com/kalenchukov/AnnotationScanning.git
```

## Версии

Доступные [версии](https://github.com/kalenchukov/AnnotationScanning/releases).

## Авторы

[Алексей Каленчуков](https://github.com/kalenchukov)

## Лицензия

[MIT](https://opensource.org/licenses/MIT)
