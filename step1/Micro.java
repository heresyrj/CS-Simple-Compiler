// Generated from Micro.g4 by ANTLR 4.5.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class Micro extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		KEYWORDS=1, OPERATORS=2, IDENTIFIER=3, INTLITERAL=4, FLOATLITERAL=5, STRINGLITERAL=6, 
		COMMENT=7;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"KEYWORDS", "OPERATORS", "LETTER", "NUMBER", "IDENTIFIER", "INTLITERAL", 
		"FLOATLITERAL", "STRINGLITERAL", "COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "KEYWORDS", "OPERATORS", "IDENTIFIER", "INTLITERAL", "FLOATLITERAL", 
		"STRINGLITERAL", "COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public Micro(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Micro.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\t\u00aa\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2i\n\2\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\5\3s\n\3\3\4\3\4\3\5\3\5\3\6\6\6z\n\6\r\6\16\6{\3"+
		"\6\3\6\7\6\u0080\n\6\f\6\16\6\u0083\13\6\3\7\6\7\u0086\n\7\r\7\16\7\u0087"+
		"\3\b\7\b\u008b\n\b\f\b\16\b\u008e\13\b\3\b\3\b\6\b\u0092\n\b\r\b\16\b"+
		"\u0093\3\t\3\t\3\t\3\t\7\t\u009a\n\t\f\t\16\t\u009d\13\t\3\t\3\t\3\t\3"+
		"\n\3\n\3\n\3\n\7\n\u00a6\n\n\f\n\16\n\u00a9\13\n\2\2\13\3\3\5\4\7\2\t"+
		"\2\13\5\r\6\17\7\21\b\23\t\3\2\7\7\2,-//\61\61<<??\6\2*+..=>@@\4\2C\\"+
		"c|\3\2$$\4\2\f\f\17\17\u00c6\2\3\3\2\2\2\2\5\3\2\2\2\2\13\3\2\2\2\2\r"+
		"\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\3h\3\2\2\2\5r\3\2\2\2"+
		"\7t\3\2\2\2\tv\3\2\2\2\13y\3\2\2\2\r\u0085\3\2\2\2\17\u008c\3\2\2\2\21"+
		"\u0095\3\2\2\2\23\u00a1\3\2\2\2\25\26\7R\2\2\26\27\7T\2\2\27\30\7Q\2\2"+
		"\30\31\7I\2\2\31\32\7T\2\2\32\33\7C\2\2\33i\7O\2\2\34\35\7D\2\2\35\36"+
		"\7G\2\2\36\37\7I\2\2\37 \7K\2\2 i\7P\2\2!\"\7G\2\2\"#\7P\2\2#i\7F\2\2"+
		"$%\7H\2\2%&\7W\2\2&\'\7P\2\2\'(\7E\2\2()\7V\2\2)*\7K\2\2*+\7Q\2\2+i\7"+
		"P\2\2,-\7T\2\2-.\7G\2\2./\7C\2\2/i\7F\2\2\60\61\7Y\2\2\61\62\7T\2\2\62"+
		"\63\7K\2\2\63\64\7V\2\2\64i\7G\2\2\65\66\7K\2\2\66i\7H\2\2\678\7G\2\2"+
		"89\7N\2\29:\7U\2\2:i\7G\2\2;<\7H\2\2<i\7K\2\2=>\7H\2\2>?\7Q\2\2?i\7T\2"+
		"\2@A\7T\2\2AB\7Q\2\2Bi\7H\2\2CD\7E\2\2DE\7Q\2\2EF\7P\2\2FG\7V\2\2GH\7"+
		"K\2\2HI\7P\2\2IJ\7W\2\2Ji\7G\2\2KL\7D\2\2LM\7T\2\2MN\7G\2\2NO\7C\2\2O"+
		"i\7M\2\2PQ\7T\2\2QR\7G\2\2RS\7V\2\2ST\7W\2\2TU\7T\2\2Ui\7P\2\2VW\7K\2"+
		"\2WX\7P\2\2Xi\7V\2\2YZ\7X\2\2Z[\7Q\2\2[\\\7K\2\2\\i\7F\2\2]^\7U\2\2^_"+
		"\7V\2\2_`\7T\2\2`a\7K\2\2ab\7P\2\2bi\7I\2\2cd\7H\2\2de\7N\2\2ef\7Q\2\2"+
		"fg\7C\2\2gi\7V\2\2h\25\3\2\2\2h\34\3\2\2\2h!\3\2\2\2h$\3\2\2\2h,\3\2\2"+
		"\2h\60\3\2\2\2h\65\3\2\2\2h\67\3\2\2\2h;\3\2\2\2h=\3\2\2\2h@\3\2\2\2h"+
		"C\3\2\2\2hK\3\2\2\2hP\3\2\2\2hV\3\2\2\2hY\3\2\2\2h]\3\2\2\2hc\3\2\2\2"+
		"i\4\3\2\2\2js\t\2\2\2kl\7#\2\2ls\7?\2\2ms\t\3\2\2no\7>\2\2os\7?\2\2pq"+
		"\7@\2\2qs\7?\2\2rj\3\2\2\2rk\3\2\2\2rm\3\2\2\2rn\3\2\2\2rp\3\2\2\2s\6"+
		"\3\2\2\2tu\t\4\2\2u\b\3\2\2\2vw\4\62;\2w\n\3\2\2\2xz\5\7\4\2yx\3\2\2\2"+
		"z{\3\2\2\2{y\3\2\2\2{|\3\2\2\2|\u0081\3\2\2\2}\u0080\5\7\4\2~\u0080\5"+
		"\t\5\2\177}\3\2\2\2\177~\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2"+
		"\u0081\u0082\3\2\2\2\u0082\f\3\2\2\2\u0083\u0081\3\2\2\2\u0084\u0086\5"+
		"\t\5\2\u0085\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0085\3\2\2\2\u0087"+
		"\u0088\3\2\2\2\u0088\16\3\2\2\2\u0089\u008b\5\t\5\2\u008a\u0089\3\2\2"+
		"\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008f"+
		"\3\2\2\2\u008e\u008c\3\2\2\2\u008f\u0091\7\60\2\2\u0090\u0092\5\t\5\2"+
		"\u0091\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094"+
		"\3\2\2\2\u0094\20\3\2\2\2\u0095\u009b\7$\2\2\u0096\u009a\5\7\4\2\u0097"+
		"\u009a\5\t\5\2\u0098\u009a\5\5\3\2\u0099\u0096\3\2\2\2\u0099\u0097\3\2"+
		"\2\2\u0099\u0098\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009b"+
		"\u009c\3\2\2\2\u009c\u009e\3\2\2\2\u009d\u009b\3\2\2\2\u009e\u009f\7$"+
		"\2\2\u009f\u00a0\n\5\2\2\u00a0\22\3\2\2\2\u00a1\u00a2\7/\2\2\u00a2\u00a3"+
		"\7/\2\2\u00a3\u00a7\3\2\2\2\u00a4\u00a6\n\6\2\2\u00a5\u00a4\3\2\2\2\u00a6"+
		"\u00a9\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\24\3\2\2"+
		"\2\u00a9\u00a7\3\2\2\2\16\2hr{\177\u0081\u0087\u008c\u0093\u0099\u009b"+
		"\u00a7\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}