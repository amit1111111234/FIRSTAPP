// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    dependencies(fun DependencyHandlerScope.() {
        implementation platform ('com.google.firebase:firebase-bom:33.1.2')
        implementation 'com.google.firebase:firebase-auth'
    })
}