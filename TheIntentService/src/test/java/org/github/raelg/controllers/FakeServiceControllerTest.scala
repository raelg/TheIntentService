package org.github.raelg.controllers

import org.junit.{Test, After, Before}
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.mockito.{Matchers, ArgumentCaptor}
import org.github.raelg.controllers.model.Weather


/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 22/07/2013
 * Time: 23:47
 */
class FakeServiceControllerTest extends BaseTestController {

    private var test: FakeServiceController = null

    @Before
    override def setUp {
        super.setUp
        test = new FakeServiceController(mockController, mockBundler)
    }

    @After
    def tearDown() {

    }

    @Test
    def testExecute() = {
        //given
        when(mockController.getResponse(mockHttpRequest)).thenReturn(
            (200, getResource("fixtures/london.json"))
        )
        when(mockIntent.getStringExtra("location")).thenReturn("London,UK")

        //when
        test.executeRequest(mockContext, mockIntent)
        //then
        verify(mockController).getRequest("http://api.openweathermap.org/data/2.5/weather?q=London,UK")

        val argument = ArgumentCaptor.forClass(classOf[Weather])
        verify(mockBundle).putSerializable(Matchers.eq(FakeServiceController.Args.Weather), argument.capture())
        val weather = argument.getValue
        print("weather: " + weather)
    }

}