package me.kiras.aimwhere.libraries.slick.svg.inkscape;

import java.util.StringTokenizer;

import me.kiras.aimwhere.libraries.slick.geom.Line;
import me.kiras.aimwhere.libraries.slick.geom.Polygon;
import me.kiras.aimwhere.libraries.slick.geom.Transform;
import me.kiras.aimwhere.libraries.slick.svg.Diagram;
import me.kiras.aimwhere.libraries.slick.svg.Figure;
import me.kiras.aimwhere.libraries.slick.svg.Loader;
import me.kiras.aimwhere.libraries.slick.svg.NonGeometricData;
import me.kiras.aimwhere.libraries.slick.svg.ParsingException;
import org.w3c.dom.Element;

/**
 * A processor for the <line> element
 *
 * @author kevin
 */
public class LineProcessor implements ElementProcessor {

	/**
	 * Process the points in a polygon definition
	 * 
	 * @param poly The polygon being built
	 * @param element The XML element being read
	 * @param tokens The tokens representing the path
	 * @return The number of points found
	 * @throws ParsingException Indicates an invalid token in the path
	 */
	private static int processPoly(Polygon poly, Element element, StringTokenizer tokens) throws ParsingException {
		int count = 0;
		
		while (tokens.hasMoreTokens()) {
			String nextToken = tokens.nextToken();
			if (nextToken.equals("L")) {
				continue;
			}
			if (nextToken.equals("z")) {
				break;
			}
			if (nextToken.equals("M")) {
				continue;
			}
			if (nextToken.equals("C")) {
				return 0;
			}
			
			String tokenX = nextToken;
			String tokenY = tokens.nextToken();
			
			try {
				float x = Float.parseFloat(tokenX);
				float y = Float.parseFloat(tokenY);
				
				poly.addPoint(x,y);
				count++;
			} catch (NumberFormatException e) {
				throw new ParsingException(element.getAttribute("id"), "Invalid token in points list", e);
			}
		}
		
		return count;
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.svg.inkscape.ElementProcessor#process(me.kiras.aimwhere.libraries.slick.svg.Loader, Element, me.kiras.aimwhere.libraries.slick.svg.Diagram, me.kiras.aimwhere.libraries.slick.geom.Transform)
	 */
	public void process(Loader loader, Element element, Diagram diagram, Transform t) throws ParsingException {
		Transform transform = Util.getTransform(element);
	    transform = new Transform(t, transform); 
		
		float x1;
		float y1;
		float x2;
		float y2;
		
		if (element.getNodeName().equals("line")) {
			x1 = Float.parseFloat(element.getAttribute("x1"));
			x2 = Float.parseFloat(element.getAttribute("x2"));
			y1 = Float.parseFloat(element.getAttribute("y1"));
			y2 = Float.parseFloat(element.getAttribute("y2"));
		} else {
			String points = element.getAttribute("d");
			StringTokenizer tokens = new StringTokenizer(points, ", ");
			Polygon poly = new Polygon();
			if (processPoly(poly, element, tokens) == 2) {
				x1 = poly.getPoint(0)[0];
				y1 = poly.getPoint(0)[1];
				x2 = poly.getPoint(1)[0];
				y2 = poly.getPoint(1)[1];
			} else {
				return;
			}
		}
		
		float[] in = new float[] {x1,y1,x2,y2};
		float[] out = new float[4];
		
		transform.transform(in,0,out,0,2);
		Line line = new Line(out[0],out[1],out[2],out[3]);
		
		NonGeometricData data = Util.getNonGeometricData(element);
		data.addAttribute("x1",""+x1);
		data.addAttribute("x2",""+x2);
		data.addAttribute("y1",""+y1);
		data.addAttribute("y2",""+y2);
		
		diagram.addFigure(new Figure(Figure.LINE, line, data, transform));
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.svg.inkscape.ElementProcessor#handles(Element)
	 */
	public boolean handles(Element element) {
		if (element.getNodeName().equals("line")) {
			return true;
		}
		if (element.getNodeName().equals("path")) {
			if (!"arc".equals(element.getAttributeNS(Util.SODIPODI, "type"))) {
				return true;
			}
		}
		
		return false;
	}
}
