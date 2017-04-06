package ru.jug.jpoint2017.alexa.jbtoolboxactivator

import com.amazon.speech.slu.Intent
import com.amazon.speech.speechlet.IntentRequest
import com.amazon.speech.speechlet.LaunchRequest
import com.amazon.speech.speechlet.Session
import com.amazon.speech.speechlet.SessionEndedRequest
import com.amazon.speech.speechlet.SessionStartedRequest
import com.amazon.speech.speechlet.Speechlet
import com.amazon.speech.speechlet.SpeechletException
import com.amazon.speech.speechlet.SpeechletResponse
import com.amazon.speech.ui.OutputSpeech
import com.amazon.speech.ui.PlainTextOutputSpeech
import com.amazon.speech.ui.Reprompt
import groovyx.net.http.HttpBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static groovyx.net.http.OkHttpBuilder.*
import static java.lang.System.getenv


/**
 * @author baruchs
 * @since 3/26/17
 */
class JbToolBoxActivatorSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger JbToolBoxActivatorSpeechlet
    final static String HELP_TEXT = 'With Jet Brains toolbox you can start Jet brains i.d.e. tools.'
    final static String DEFAULT_QUESTION = 'Now, which tool do you want me to start?'
    public static final String TOOL = 'Tool'
    private HttpBuilder httpBuilder
    private List<String> tools


    @Override
    void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
        log.info "onSessionStarted requestId=$sessionStartedRequest.requestId, sessionId=$session.sessionId"

        httpBuilder = configure {
            request.uri = "http://${getenv('TOOLBOX_IP')}:5050"
        }

        tools = new File(this.class.classLoader.getResource('speechAssets/ListOfTools.txt').toURI()).readLines()
    }

    @Override
    SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info "onLaunch requestId=$request.requestId, sessionId=$session.sessionId"
        newAskResponse("I am Jet brains toolbox and I can start Jet brains tools for you. $DEFAULT_QUESTION", HELP_TEXT + DEFAULT_QUESTION)
    }

    @Override
    SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
        log.info "onIntent requestId=$intentRequest.requestId, sessionId=$session.sessionId"
        Intent intent = intentRequest.intent
        switch (intent.name) {
            case 'OpenIntent':
                PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech()
                def toolName = intent.getSlot(TOOL).value
                if (tools.contains(toolName)) {
                    outputSpeech.text = "Opening $toolName. Goodbye."
//                    httpBuilder.post {
//                        request.uri = "/${toolName}"
//                    }
                } else {
                    outputSpeech.text = "Sorry, I can't find a tool named $toolName in the toolbox. Goodbye."
                }
                return new SpeechletResponse().newTellResponse(outputSpeech)
            case 'AMAZON.HelpIntent':
                return newAskResponse("I can open tools like $tools. $DEFAULT_QUESTION", DEFAULT_QUESTION)
            case 'AMAZON.StopIntent':
            case 'AMAZON.CancelIntent':
                PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech()
                outputSpeech.text = 'Goodbye'
                return new SpeechletResponse().newTellResponse(outputSpeech)
            default:
                throw new SpeechletException('Invalid Intent')

        }
    }


    @Override
    void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId())
    }

    static SpeechletResponse newAskResponse(String outputSpeechText, String repromptText) {
        OutputSpeech speech = new PlainTextOutputSpeech()
        speech.text = outputSpeechText
        OutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech()
        repromptOutputSpeech.text = repromptText
        Reprompt reprompt = new Reprompt()
        reprompt.outputSpeech = repromptOutputSpeech
        SpeechletResponse.newAskResponse(speech, reprompt)
    }
}
