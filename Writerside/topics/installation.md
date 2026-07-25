# Installation
<link-summary>Guide to adding Never Enough Recipes to your project dependencies and paper-plugin.yml</link-summary>

Integrating Never Enough Recipes (NER) into your plugin allows you to register your custom items (into the GUI), recipe layouts, and item groups.

To get started, you will need to complete two steps:
1. Add the Gradle or Maven dependency to your build script.
2. Add the `paper-plugin.yml` dependency so the server loads NER first.

### Adding the Build Dependency

Since version 1.3.0, the API is hosted directly on Maven Central, so you don't need to add any other repositories to your build script.

<tabs>
<tab title="Gradle (Kotlin)">
<code-block lang="kotlin">
dependencies {
    implementation("com.github.darksoulq:NeverEnoughRecipes:&lt;version&gt;")
}
</code-block>
</tab>
<tab title="Gradle (Groovy)">
<code-block lang="gradle">
dependencies {
    implementation 'com.github.darksoulq:NeverEnoughRecipes:&lt;version&gt;'
}
</code-block>
</tab>
<tab title="Maven">
<code-block lang="xml">
&lt;dependency&gt;
    &lt;groupId&gt;com.github.darksoulq&lt;/groupId&gt;
    &lt;artifactId&gt;NeverEnoughRecipes&lt;/artifactId&gt;
    &lt;version&gt;&lt;version&gt;&lt;/version&gt;
&lt;/dependency&gt;
</code-block>
</tab>
</tabs>

<note>
Make sure to replace <code>&lt;version&gt;</code> with the latest release version found on GitHub.
</note>

### Adding the paper-plugin.yml Dependency

Since NER handles registry compilation and UI setups early in the server lifecycle, your plugin needs to tell Paper to load it first.

<tip>
Setting <code>load: BEFORE</code> ensures that NER's internal registries and event buses are fully initialized before your plugin attempts to register its custom content in your <code>onEnable()</code> method.
</tip>

<code-block lang="yaml">
dependencies:
  server:
    NeverEnoughRecipes:
      required: true
      load: BEFORE
  bootstrap:
    NeverEnoughRecipes:
      required: true
      load: BEFORE
</code-block>