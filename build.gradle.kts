// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(Modules.Plugins.androidApplication) version Modules.Plugins.Version.androidApplication apply false
    id(Modules.Plugins.androidLibrary) version Modules.Plugins.Version.androidLibrary apply false
    id(Modules.Plugins.kotlinAndroid) version Modules.Plugins.Version.kotlinAndroid apply false
    id(Modules.Plugins.mapsPlatform) version Modules.Plugins.Version.mapsPlatform apply false
}

tasks.register("clean").configure {
    delete("build")
}