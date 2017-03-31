package ru.jug.jpoint2017.alexa.jbtoolboxactivator

import com.amazon.speech.speechlet.Speechlet
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler
import groovy.transform.InheritConstructors

/**
 * @author baruchs
 * @since 3/30/17
 */
@InheritConstructors
class JbToolBoxActivatorSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds = ['arn:aws:lambda:us-east-1:747436124119:function:JetBrainsToolbox']

    JbToolBoxActivatorSpeechletRequestStreamHandler() {
        super(new JbToolBoxActivatorSpeechlet(), supportedApplicationIds)
    }
}

