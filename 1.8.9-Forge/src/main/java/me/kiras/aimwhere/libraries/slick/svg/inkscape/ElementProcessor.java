package me.kiras.aimwhere.libraries.slick.svg.inkscape;

import me.kiras.aimwhere.libraries.slick.geom.Transform;
import me.kiras.aimwhere.libraries.slick.svg.Diagram;
import me.kiras.aimwhere.libraries.slick.svg.Loader;
import me.kiras.aimwhere.libraries.slick.svg.ParsingException;
import org.w3c.dom.Element;

/**
 * The description of a module which processes a single XML element from a SVG (inkscape) 
 * document.
 *
 * @author kevin
 */
public interface ElementProcessor {
	/**
	 * Process a document extracting all the elements that the processor is 
	 * interested in and producing appropriate diagram components for the
	 * element.
	 * 
	 * @param loader The loader/context of the parsing
	 * @param element The element to be processed
	 * @param diagram The diagram to be built
	 * @param transform The transform to apply to all elements at this level
	 * @throws ParsingException Indicates an invalid content to an element
	 */
	public void process(Loader loader, Element element, Diagram diagram, Transform transform) throws ParsingException;

	/**
	 * Check if this processor handles the element specified
	 * 
	 * @param element The element to check
	 * @return True if this processor can handle the given element
	 */
	public boolean handles(Element element);
}
