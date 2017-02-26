package com.mapeiyu

import org.gradle.api.tasks.Input

class Rule {
    final String name
    List<String> excludeList = []

    Rule(String name) {
        this.name = name
    }

    @Input
    void exclude(String...args) {
        excludeList += Arrays.asList(args)
    }
}