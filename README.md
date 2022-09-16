
## Limitations

It's possible that the code were not generated correctly in some cases.
Please create an issue if you've found any wrong parameter/return type etc.

### Enums

Enums will be generated as well, however if any function/event returns an enum like:

```kotlin
browser.runtime.getPlatformInfo()
```

The enum won't be accessible as such.
Instead, call the ```toString()``` method on it and then create the enum type by this value, e.g.:

```kotlin
browser.runtime.getPlatformInfo().collect {
    console.log(it.os == PlatformOs.linux)
    console.log(PlatformOs.valueOf(it.os.toString()) == PlatformOs.linux)
}
```

This will output:

```
false
true
```

## Build

### Generate APIs

This is useful e.g. if the API specifications changed.

##### Make sure the previous generated files are deleted

```bash
./gradlew :generate:clean
```

##### Generate the files by its specifications

```bash
./gradlew :generate:run
```

### Export your extension

##### Rebuild JS files

This step is not needed if you've edited resource files only.

```bash
./gradlew clean assemble
```

##### Copy needed files to /build/extension

```bash
./gradlew extension
```

## Contributing

When you face any bugs or problems please open an [Issue](https://github.com/DATL4G/BurningSeries-Android/issues/new/choose).

To add functionality fork the project and create a pull request afterwards. You should know how that works if you are a developer :)
You can add yourself to the list below if you want then.

### Maintainers

| Avatar | Contributor |
|---|:---:|
| [![](https://avatars3.githubusercontent.com/u/46448715?s=50&v=4)](http://github.com/DatL4g) | [DatLag](http://github.com/DatL4g) |

## Support the project

[![Github-sponsors](https://img.shields.io/badge/sponsor-30363D?style=for-the-badge&logo=GitHub-Sponsors&logoColor=#EA4AAA)](https://github.com/sponsors/DATL4G)
[![PayPal](https://img.shields.io/badge/PayPal-00457C?style=for-the-badge&logo=paypal&logoColor=white)](https://paypal.me/datlag)
[![Patreon](https://img.shields.io/badge/Patreon-F96854?style=for-the-badge&logo=patreon&logoColor=white)](https://www.patreon.com/datlag)

Supporting this project helps to keep it up-to-date. You can donate if you want or contribute to the project as well.
This shows that the app is used by people and it's worth to maintain.
