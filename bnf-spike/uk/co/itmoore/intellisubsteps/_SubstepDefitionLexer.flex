package uk.co.itmoore.intellisubsteps;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static uk.co.itmoore.intellisubsteps.psi.SubstepDefTypes.*;

%%

%{
  public _SubstepDefitionLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class SubstepDefitionLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

STEPLINE=([^#]#*?.*)

%%
<YYINITIAL> {
  {WHITE_SPACE}      { return com.intellij.psi.TokenType.WHITE_SPACE; }


  {STEPLINE}         { return STEPLINE; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
