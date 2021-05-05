package me.kiras.aimwhere.libraries.slick.svg.inkscape;

import me.kiras.aimwhere.libraries.slick.geom.Transform;
import me.kiras.aimwhere.libraries.slick.svg.Diagram;
import me.kiras.aimwhere.libraries.slick.svg.Loader;
import me.kiras.aimwhere.libraries.slick.svg.ParsingException;
import org.w3c.dom.Element;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class GroupProcessor implements ElementProcessor {

	/**
	 * @see me.kiras.aimwhere.libraries.slick.svg.inkscape.ElementProcessor#handles(Element)
	 */
	public boolean handles(Element element) {
		if (element.getNodeName().equals("g")) {
			return true;
		}
		return false;
	}

	/**O
	 * @see me.kiras.aimwhere.libraries.slick.svg.inkscape.ElementProcessor#process(me.kiras.aimwhere.libraries.slick.svg.Loader, Element, me.kiras.aimwhere.libraries.slick.svg.Diagram, me.kiras.aimwhere.libraries.slick.geom.Transform)
	 */
	public void process(Loader loader, Element element, Diagram diagram, Transform t) throws ParsingException {
		Transform transform = Util.getTransform(element);
		transform = new Transform(t, transform);
		
		loader.loadChildren(element, transform);
	}

}
