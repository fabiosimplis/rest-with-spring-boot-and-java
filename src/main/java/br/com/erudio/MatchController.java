package br.com.erudio;

import br.com.erudio.Math.SimpleMath;
import br.com.erudio.exceptions.UnsupportedMathOperationException;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

import static br.com.erudio.converters.NumberConverter.convetToDouble;
import static br.com.erudio.converters.NumberConverter.isNumeric;


@RestController
public class MatchController {

    private final AtomicLong counter = new AtomicLong();
    private SimpleMath math  = new SimpleMath();

    @RequestMapping(value = "/sum/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double sum(
            @PathVariable String numberOne,
            @PathVariable String numberTwo
    ) throws Exception {

        if (!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric number");
        }

        return math.sum(convetToDouble(numberOne), convetToDouble(numberTwo));
    }

    @RequestMapping(value = "/subtraction/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double subtraction(
            @PathVariable String numberOne,
            @PathVariable String numberTwo
    ) throws Exception {

        if (!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric number");
        }

        return math.subtraction(convetToDouble(numberOne), convetToDouble(numberTwo));
    }

    @RequestMapping(value = "/multiply/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double multiply(
            @PathVariable String numberOne,
            @PathVariable String numberTwo
    ) throws Exception {

        if (!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric number");
        }

        return math.multiply(convetToDouble(numberOne), convetToDouble(numberTwo));
    }

    @RequestMapping(value = "/division/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double division(
            @PathVariable String numberOne,
            @PathVariable String numberTwo
    ) throws Exception {

        if (!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric number");
        }

        Double number2 = convetToDouble(numberTwo);
        if (number2 == 0D)
            throw new UnsupportedMathOperationException("Please set a numeric number greater than 0");

        return math.division(convetToDouble(numberOne), convetToDouble(numberTwo));
    }

    @RequestMapping(value = "/average/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double average(
            @PathVariable String numberOne,
            @PathVariable String numberTwo
    ) throws Exception {

        if (!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please set a numeric number");
        }

        return math.average(convetToDouble(numberOne), convetToDouble(numberTwo));
    }

    @RequestMapping(value = "/squareRoot/{number}", method = RequestMethod.GET)
    public Double squareRoot(
            @PathVariable String number
    ) throws Exception {

        if (!isNumeric(number)){
            throw new UnsupportedMathOperationException("Please set a numeric number");
        }

        return math.squareRoot(convetToDouble(number));
    }

}
