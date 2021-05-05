package me.kiras.aimwhere.libraries.slick.svg.inkscape;

import me.kiras.aimwhere.libraries.slick.geom.Ellipse;
import me.kiras.aimwhere.libraries.slick.geom.Shape;
import me.kiras.aimwhere.libraries.slick.geom.Transform;
import me.kiras.aimwhere.libraries.slick.svg.Diagram;
import me.kiras.aimwhere.libraries.slick.svg.Figure;
import me.kiras.aimwhere.libraries.slick.svg.Loader;
import me.kiras.aimwhere.libraries.slick.svg.NonGeometricData;
import me.kiras.aimwhere.libraries.slick.svg.ParsingException;
import org.w3c.dom.Element;

/**
 * Processor for <ellipse> and <path> nodes marked as arcs
 *
 * @author kevin
 */
public class EllipseProcessor implements ElementProcessor {
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.svg.inkscape.ElementProcessor#process(me.kiras.aimwhere.libraries.slick.svg.Loader, Element, me.kiras.aimwhere.libraries.slick.svg.Diagram, me.kiras.aimwhere.libraries.slick.geom.Transform)
	 */
	public void process(Loader loader, Element element, Diagram diagram, Transform t) throws ParsingException {
		Transform transform = Util.getTransform(element);
		transform = new Transform(t, transform);
		
		float x = Util.getFloatAttribute(element,"cx");
		float y = Util.getFloatAttribute(element,"cy");
		float rx = Util.getFloatAttribute(element,"rx");
		float ry = Util.getFloatAttribute(element,"ry");
		
		Ellipse ellipse = new Ellipse(x,y,rx,ry);
		Shape shape = ellipse.transform(transform);

		NonGeometricData data = Util.getNonGeometricData(element);
		data.addAttribute("cx", ""+x);
		data.addAttribute("cy", ""+y);
		data.addAttribute("rx", ""+rx);
		data.addAttribute("ry", ""+ry);
		
		diagram.addFigure(new Figure(Figure.ELLIPSE, shape, data, transform));
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.svg.inkscape.ElementProcessor#handles(Element)
	 */
	public boolean handles(Element element) {
		if (element.getNodeName().equals("ellipse")) {
			return true;
		}
		if (element.getNodeName().equals("path")) {
			if ("arc".equals(element.getAttributeNS(Util.SODIPODI, "type"))) {
				return true;
			}
		}
		
		return false;
	}

}
