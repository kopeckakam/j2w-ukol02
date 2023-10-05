package cz.czechitas.java2webapps.ukol2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

@Controller
public class MainController {
    private Random random = new Random();
    @GetMapping("/")
    public ModelAndView displayQuote() throws IOException {
        List<String> quotes = readAllLines("citaty.txt");
        int quoteIndex = random.nextInt(0, quotes.size());
        int imageIndex = random.nextInt(1, quotes.size() + 1);
        List<String> splitQuote = parseLine(quotes.get(quoteIndex));

        String modelAndViewName;
        if (splitQuote.get(0).isEmpty()) {
            modelAndViewName = "plainQuote";
        } else {
            modelAndViewName = "quote";
        }

        ModelAndView result = new ModelAndView(modelAndViewName);
        result.addObject("quoteTitle", splitQuote.get(0));
        result.addObject("quotePron", splitQuote.get(1));
        result.addObject("quoteWordType", splitQuote.get(2));
        result.addObject("quoteText", splitQuote.get(3));
        result.addObject("imageURL", String.format("/images/image%d.jpg", (imageIndex)));

        return result;
    }

    private static List<String> readAllLines(String resource)throws IOException {
        //Soubory z resources se získávají pomocí classloaderu. Nejprve musíme získat aktuální classloader.
        ClassLoader classLoader=Thread.currentThread().getContextClassLoader();

        //Pomocí metody getResourceAsStream() získáme z classloaderu InpuStream, který čte z příslušného souboru.
        //Následně InputStream převedeme na BufferedRead, který čte text v kódování UTF-8
        try(InputStream inputStream=classLoader.getResourceAsStream(resource);
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){

            //Metoda lines() vrací stream řádků ze souboru. Pomocí kolektoru převedeme Stream<String> na List<String>.
            return reader
                    .lines()
                    .collect(Collectors.toList());
        }
    }

    private static List<String> parseLine(String line) {

        String quoteTitle = "";
        String quotePron = "";
        String quoteWordType = "";
        String quoteText = "";
        String currLine = line;

        int startIndex = line.indexOf(':');
        if (startIndex > -1) {
            quoteText = currLine.substring(startIndex + 1);
            currLine = currLine.substring(0, startIndex);
        } else {
            return List.of(quoteTitle,quotePron,quoteWordType,currLine);
        }

        startIndex = line.indexOf('(');
        int endIndex = line.indexOf(')');
        if (startIndex > -1 && endIndex > - 1) {
            quoteWordType = currLine.substring(startIndex, endIndex + 1);
            currLine = currLine.substring(0, startIndex);
        }

        startIndex = line.indexOf(" /");
        endIndex = line.indexOf("/ ");
        if ((startIndex > -1) && (endIndex > - 1)) {
            quotePron = (currLine.substring(startIndex, endIndex + 1));
            currLine = currLine.substring(0, startIndex);
        }

        quoteTitle = currLine;

        return List.of(quoteTitle,quotePron,quoteWordType,quoteText);

    }
}
