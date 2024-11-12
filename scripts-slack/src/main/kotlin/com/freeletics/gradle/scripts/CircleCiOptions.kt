package com.freeletics.gradle.scripts

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

public class CircleCiOptions : OptionGroup("CircleCI options") {
    private val userName by option(
        "--project-username",
        envvar = "CIRCLE_PROJECT_USERNAME",
        help = "The username that this project belongs to",
    ).required()

    private val repoName by option(
        "--project-reponame",
        envvar = "CIRCLE_PROJECT_REPONAME",
        help = "The name of the repository for this project",
    ).required()

    public val jobUrl: String by option(
        "--job-url",
        envvar = "CIRCLE_BUILD_URL",
        help = "The url to the job",
    ).required()

    public val jobName: String by option(
        "--job-name",
        envvar = "CIRCLE_JOB",
        help = "The name of the job",
    ).required()

    private val buildNumber by option(
        "--build-number",
        envvar = "CIRCLE_BUILD_NUM",
        help = "The number of the job",
    ).required()

    public val branch: String by option(
        "--branch",
        envvar = "CIRCLE_BRANCH",
        help = "The branch that was built",
    ).required()

}
