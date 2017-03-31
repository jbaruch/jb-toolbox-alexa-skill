package ru.jug.jpoint2017.alexa.jbtoolboxactivator

import org.junit.Test

/**
 * @author baruchs
 * @since 3/31/17
 */
class JbToolBoxActivatorSpeechletTest {
    @Test
     void 'test new Ask Response'() {
        def response = JbToolBoxActivatorSpeechlet.newAskResponse('a', 'b')
        assert response.reprompt.outputSpeech.text == 'b'
        assert response.outputSpeech.text == 'a'
    }

}