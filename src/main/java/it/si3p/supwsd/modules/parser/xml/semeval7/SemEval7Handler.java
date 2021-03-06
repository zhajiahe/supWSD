package it.si3p.supwsd.modules.parser.xml.semeval7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import it.si3p.supwsd.data.Annotation;
import it.si3p.supwsd.data.Lexel;
import it.si3p.supwsd.modules.parser.xml.XMLHandler;

/**
 * @author papandrea
 *
 */
public class SemEval7Handler extends XMLHandler {

	protected String mSentenceID,mInstanceID,mSentence;
	protected final List<Lexel>mLexels;
	protected final Map<String,HashSet<Annotation>>mAnnotations;
	protected String mInstance,mLemma,mPOS;
	
	public SemEval7Handler() {

		mLexels = new ArrayList<Lexel>();
		mAnnotations=new HashMap<String,HashSet<Annotation>>();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) {

		SemEval7Tag tag;

		tag = SemEval7Tag.valueOf(name.toUpperCase());

		switch (tag) {

		case INSTANCE:

			mInstance="";
			mInstanceID = attributes.getValue(SemEval7Attribute.ID.name().toLowerCase());
			mLemma=attributes.getValue(SemEval7Attribute.LEMMA.name().toLowerCase());
			mPOS=attributes.getValue(SemEval7Attribute.POS.name().toLowerCase());
			break;

		case SENTENCE:

			mSentence = "";
			mSentenceID= attributes.getValue(SemEval7Attribute.ID.name().toLowerCase());
			break;
			
		default:
			break;
		}

		this.push(tag);
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {

		SemEval7Tag tag = SemEval7Tag.valueOf(name.toUpperCase());

		switch (tag) {

		case CORPUS:

			notifyAnnotations();
			break;
			
		case SENTENCE:

			addAnnotation();
			break;

		case INSTANCE:
			
			addWord(formatAnnotation(mInstance));
			addInstance(formatInstance(mLemma)+"."+mPOS);
			break;

		default:
			break;
		}

		this.pop();
	}

	@Override
	public void characters(char ch[], int start, int length) {

		String word;

		switch ((SemEval7Tag) this.get()) {

		case SENTENCE:

			word = new String(ch, start, length).replaceAll("[\r\n]", " ");
			addWord(word);
			break;

		case INSTANCE:
			
			word = new String(ch, start, length).replaceAll("[\r\n]", " ");
			mInstance+=word;
			break;
	
		default:
			break;
		}
	}

	protected final void addWord(String word) {

		word=word.trim();
		
		if (!word.isEmpty()) 
			mSentence += word+" ";		
	}
	
	protected Lexel addInstance(String instance) {

		Lexel lexel;
		
		lexel=new Lexel(mInstanceID, instance.trim());
		mLexels.add(lexel);
		
		return lexel;
	}

	protected String formatInstance(String lemma){
		
		return lemma.trim().replaceAll("[\\s\\-]", "_").toLowerCase();
	}
	
	protected final void notifyAnnotations() throws SAXException {
		
		try {
			
			for(Entry<String, HashSet<Annotation>> entry:mAnnotations.entrySet())
			mAnnotationListener.notifyAnnotations(new ArrayList<Annotation>(entry.getValue()),entry.getKey());
		
		} catch (Exception e) {
			throw new SAXException(e);
		}
		
		mAnnotations.clear();
	}

	protected final void addAnnotation() {

		Annotation annotation;
		HashSet<Annotation>annotations;
		String name;
		
		annotation=new Annotation(mSentenceID,mSentence.trim());
		annotation.addLexels(mLexels);		

		for(Lexel lexel:mLexels) {
		
			name=lexel.toString();
			annotations=mAnnotations.get(name);
			
			if(annotations==null) {
				annotations=new HashSet<Annotation>();
				mAnnotations.put(name, annotations);
			}
		
			annotations.add(annotation);
		}
		
		mLexels.clear();
	}
}
