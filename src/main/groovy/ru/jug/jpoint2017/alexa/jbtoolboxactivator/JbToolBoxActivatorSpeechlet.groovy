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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static groovyx.net.http.HttpBuilder.configure
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


    @Override
    void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        log.info "onSessionStarted requestId=$request.requestId, sessionId=$session.sessionId"


    }

    @Override
    SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info "onLaunch requestId=$request.requestId, sessionId=$session.sessionId"

        newAskResponse('I am Jet brains toolbox and I can start Jet brains tools for you. What tool should I start?', HELP_TEXT + DEFAULT_QUESTION)
    }

    @Override
    SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        log.info "onIntent requestId=$request.requestId, sessionId=$session.sessionId"
        Intent intent = request.intent
        switch (intent.name) {
            case 'OpenIntent':
                def toolName = intent.getSlot(TOOL).value
                PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech()
                outputSpeech.text = "Opening $toolName"
                open(toolName)
                new SpeechletResponse().newTellResponse(outputSpeech)
                break
            case 'AMAZON.HelpIntent':
                newAskResponse(HELP_TEXT, DEFAULT_QUESTION)
            case 'AMAZON.StopIntent':
            case 'AMAZON.CancelIntent':
                PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech()
                outputSpeech.text = 'Goodbye'
                new SpeechletResponse().newTellResponse(outputSpeech)
                break
            default:
                throw new SpeechletException('Invalid Intent')

        }
    }

    void open(String toolName) {
        configure {
            request.uri = "${getenv('TOOLBOX_IP')}:5050/$toolName"
        }.post()
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
