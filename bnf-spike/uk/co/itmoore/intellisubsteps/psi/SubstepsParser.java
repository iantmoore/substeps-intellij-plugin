package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubstepsParser implements PsiParser {

  @NotNull
  public ASTNode parse(IElementType root, PsiBuilder builder) {
    final PsiBuilder.Marker marker = builder.mark();
    parseFileTopLevel(builder);
    marker.done(SubstepsElementTypes.SUBSTEPS_FILE);
    return builder.getTreeBuilt();
  }

  private static void parseFileTopLevel(PsiBuilder builder) {
    while(!builder.eof()) {
      final IElementType tokenType = builder.getTokenType();
      if (tokenType == SubstepsTokenTypes.FEATURE_KEYWORD) {
        parseFeature(builder);
      } else if (tokenType == SubstepsTokenTypes.TAG) {
        parseTags(builder);
      } else {
        builder.advanceLexer();
      }
    }
  }

  private static void parseFeature(PsiBuilder builder) {
    final PsiBuilder.Marker marker = builder.mark();

    assert builder.getTokenType() == SubstepsTokenTypes.FEATURE_KEYWORD;
    final int featureEnd = builder.getCurrentOffset() + getTokenLength(builder.getTokenText());

    PsiBuilder.Marker descMarker = null;
    while(true) {
      final IElementType tokenType = builder.getTokenType();
      if (tokenType == SubstepsTokenTypes.TEXT && descMarker == null) {
        if (hadLineBreakBefore(builder, featureEnd)) {
          descMarker = builder.mark();
        }
      }

      if (tokenType == SubstepsTokenTypes.SCENARIO_KEYWORD ||
          tokenType == SubstepsTokenTypes.BACKGROUND_KEYWORD ||
          tokenType == SubstepsTokenTypes.SCENARIO_OUTLINE_KEYWORD ||
          tokenType == SubstepsTokenTypes.TAG) {
        if (descMarker != null) {
          descMarker.done(SubstepsElementTypes.FEATURE_HEADER);
          descMarker = null;
        }
        parseFeatureElements(builder);
      }
      builder.advanceLexer();
      if (builder.eof()) break;
    }
    if (descMarker != null) {
      descMarker.done(SubstepsElementTypes.FEATURE_HEADER);
    }
    marker.done(SubstepsElementTypes.FEATURE);
  }

  private static boolean hadLineBreakBefore(PsiBuilder builder, int prevTokenEnd) {
    if (prevTokenEnd < 0) return false;
    final String precedingText = builder.getOriginalText().subSequence(prevTokenEnd, builder.getCurrentOffset()).toString();
    return precedingText.contains("\n");
  }

  private static void parseTags(PsiBuilder builder) {
    while (builder.getTokenType() == SubstepsTokenTypes.TAG) {
      final PsiBuilder.Marker tagMarker = builder.mark();
      builder.advanceLexer();
      tagMarker.done(SubstepsElementTypes.TAG);
    }
  }

  private static void parseFeatureElements(PsiBuilder builder) {
    while (!builder.eof()) {
      final PsiBuilder.Marker marker = builder.mark();
      // tags
      parseTags(builder);

      // scenarios
      IElementType startTokenType = builder.getTokenType();
      final boolean outline = startTokenType == SubstepsTokenTypes.SCENARIO_OUTLINE_KEYWORD;
      builder.advanceLexer();
      parseScenario(builder, outline);
      marker.done(outline ? SubstepsElementTypes.SCENARIO_OUTLINE : SubstepsElementTypes.SCENARIO);
    }
  }

  private static void parseScenario(PsiBuilder builder, boolean outline) {
    while (!atScenarioEnd(builder)) {
      if (builder.getTokenType() == SubstepsTokenTypes.TAG) {
        final PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();
        if (atScenarioEnd(builder)) {
          marker.rollbackTo();
          break;
        } else {
          marker.drop();
        }
      }

      if (builder.getTokenType() == SubstepsTokenTypes.STEP_KEYWORD) {
        parseStep(builder);
      }
      else if (builder.getTokenType() == SubstepsTokenTypes.EXAMPLES_KEYWORD && outline) {
        parseExamplesBlock(builder);
      }
      else {
        builder.advanceLexer();
      }
    }
  }

  private static boolean atScenarioEnd(PsiBuilder builder) {
    int i = 0;
    while (builder.lookAhead(i) == SubstepsTokenTypes.TAG) {
      i++;
    }
    final IElementType tokenType = builder.lookAhead(i);
    return tokenType == null ||
           tokenType == SubstepsTokenTypes.BACKGROUND_KEYWORD ||
           tokenType == SubstepsTokenTypes.SCENARIO_KEYWORD ||
           tokenType == SubstepsTokenTypes.SCENARIO_OUTLINE_KEYWORD;
  }

  private static void parseStep(PsiBuilder builder) {
    final PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    int prevTokenEnd = -1;
    while (builder.getTokenType() == SubstepsTokenTypes.TEXT || builder.getTokenType() == SubstepsTokenTypes.STEP_PARAMETER_BRACE
           || builder.getTokenType() == SubstepsTokenTypes.STEP_PARAMETER_TEXT) {
      String tokenText = builder.getTokenText();
      if (hadLineBreakBefore(builder, prevTokenEnd)) {
        break;
      }
      prevTokenEnd = builder.getCurrentOffset() + getTokenLength(tokenText);
      if (builder.getTokenType() == SubstepsTokenTypes.STEP_PARAMETER_TEXT) {
        final PsiBuilder.Marker stepParameterMarker = builder.mark();
        builder.advanceLexer();
        stepParameterMarker.done(SubstepsElementTypes.STEP_PARAMETER);
      } else {

        builder.advanceLexer();
      }
    }
    final IElementType tokenTypeAfterName = builder.getTokenType();
    if (tokenTypeAfterName == SubstepsTokenTypes.PIPE) {
      parseTable(builder);
    }
// else if (tokenTypeAfterName == SubstepsTokenTypes.PYSTRING) {
//      parsePystring(builder);
//    }
    marker.done(SubstepsElementTypes.STEP);
  }

//  private static void parsePystring(PsiBuilder builder) {
//    if (!builder.eof()) {
//      final PsiBuilder.Marker marker = builder.mark();
//      builder.advanceLexer();
//      while (!builder.eof() && builder.getTokenType() != SubstepsTokenTypes.PYSTRING) {
//        if (builder.getTokenType() == SubstepsTokenTypes.STEP_PARAMETER_TEXT) {
//          final PsiBuilder.Marker stepParameterMarker = builder.mark();
//          builder.advanceLexer();
//          stepParameterMarker.done(SubstepsElementTypes.STEP_PARAMETER);
//        } else {
//          builder.advanceLexer();
//        }
//      }
//      if (!builder.eof()) {
//        builder.advanceLexer();
//      }
//      marker.done(SubstepsElementTypes.PYSTRING);
//    }
//  }

  private static void parseExamplesBlock(PsiBuilder builder) {
    final PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    if (builder.getTokenType() == SubstepsTokenTypes.COLON) builder.advanceLexer();
    while(builder.getTokenType() == SubstepsTokenTypes.TEXT) {
      builder.advanceLexer();
    }
    if (builder.getTokenType() == SubstepsTokenTypes.PIPE) {
      parseTable(builder);
    }
    marker.done(SubstepsElementTypes.EXAMPLES_BLOCK);
  }

  private static void parseTable(PsiBuilder builder) {
    final PsiBuilder.Marker marker = builder.mark();
    PsiBuilder.Marker rowMarker = builder.mark();
    int prevCellEnd = -1;
    boolean isHeaderRow = true;
    PsiBuilder.Marker cellMarker = null;

    IElementType prevToken = null;
    while (builder.getTokenType() == SubstepsTokenTypes.PIPE || builder.getTokenType() == SubstepsTokenTypes.TABLE_CELL) {
      final IElementType tokenType = builder.getTokenType();

      final boolean hasLineBreakBefore = hadLineBreakBefore(builder, prevCellEnd);

      // cell - is all between pipes
      if (prevToken == SubstepsTokenTypes.PIPE) {
        // Don't start new cell if prev was last in the row
        // it's not a cell, we just need to close a row
        if (!hasLineBreakBefore) {
          cellMarker = builder.mark();
        }
      }
      if (tokenType == SubstepsTokenTypes.PIPE) {
        if (cellMarker != null) {
          closeCell(cellMarker);
          cellMarker = null;
        }
      }

      if (hasLineBreakBefore) {
        closeRowMarker(rowMarker, isHeaderRow);
        isHeaderRow = false;
        rowMarker = builder.mark();
      }
      prevCellEnd = builder.getCurrentOffset() + getTokenLength(builder.getTokenText());
      prevToken = tokenType;
      builder.advanceLexer();
    }

    if (cellMarker != null) {
      closeCell(cellMarker);
    }
    closeRowMarker(rowMarker, isHeaderRow);
    marker.done(SubstepsElementTypes.TABLE);
  }

  private static void closeCell(PsiBuilder.Marker cellMarker) {
    cellMarker.done(SubstepsElementTypes.TABLE_CELL);
  }

  private static void closeRowMarker(PsiBuilder.Marker rowMarker, boolean headerRow) {
    rowMarker.done(headerRow ? SubstepsElementTypes.TABLE_HEADER_ROW : SubstepsElementTypes.TABLE_ROW);
  }

  private static int getTokenLength(@Nullable final String tokenText) {
    return tokenText != null ? tokenText.length() : 0;
  }
}
