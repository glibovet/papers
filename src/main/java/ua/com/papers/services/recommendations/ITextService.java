package ua.com.papers.services.recommendations;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.List;

public interface ITextService {
    List<String> breakTextIntoUniGramsAndBiGrams(String text);
}
