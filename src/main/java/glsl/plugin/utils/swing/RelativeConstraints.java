package glsl.plugin.utils.swing;

import java.awt.*;

/**
 * @see <a href="https://gist.github.com/DrBrad/e4613ccf8f06ec53047a404834a4d94a">Relative Layout</a>
 * @author <a href="https://gist.github.com/DrBrad">DrBrad</a>
 */
public class RelativeConstraints {
	public static int MATCH_PARENT = -2, WRAP_CONTENT = -1, MATCH_COUNTER_SIDE = -3;

	public Dimension size;
	public Insets margins;
	public double aspectRatio = -1;

	public int xAlignment = -1, xAlignment1 = -1, yAlignment = -1, yAlignment1 = -1;
	public Component xComponent, xComponent1, yComponent, yComponent1;

	public RelativeConstraints(){
		size = new Dimension(WRAP_CONTENT, WRAP_CONTENT);
		margins = new Insets(0, 0, 0, 0);
	}

	public RelativeConstraints setSize(Dimension size){
		this.size = size;
		return this;
	}

	public RelativeConstraints setSize(Dimension size, boolean keepAspectRatio){
		this.size = size;
		this.aspectRatio = keepAspectRatio ? size.getWidth() / (double) size.getHeight() : -1;
		return this;
	}

	public RelativeConstraints setAspectRatio(double aspectRatio){
		this.aspectRatio = aspectRatio;
		return this;
	}

	public RelativeConstraints setWidth(int width){
		size.width = width;
		return this;
	}

	public RelativeConstraints setHeight(int height){
		size.height = height;
		return this;
	}

	public RelativeConstraints alignParentTop(){
		updateYAlignment(0, null);
		return this;
	}

	public RelativeConstraints alignParentLeft(){
		updateXAlignment(0, null);
		return this;
	}

	public RelativeConstraints alignParentRight(){
		updateXAlignment(1, null);
		return this;
	}

	public RelativeConstraints alignParentBottom(){
		updateYAlignment(1, null);
		return this;
	}

	public RelativeConstraints alignTop(Component component){
		updateYAlignment(2, component);
		return this;
	}

	public RelativeConstraints alignLeft(Component component){
		updateXAlignment(2, component);
		return this;
	}

	public RelativeConstraints alignRight(Component component){
		updateXAlignment(3, component);
		return this;
	}

	public RelativeConstraints alignBottom(Component component){
		updateYAlignment(3, component);
		return this;
	}

	public RelativeConstraints above(Component component){
		updateYAlignment(4, component);
		return this;
	}

	public RelativeConstraints toLeftOf(Component component){
		updateXAlignment(4, component);
		return this;
	}

	public RelativeConstraints toRightOf(Component component){
		updateXAlignment(5, component);
		return this;
	}

	public RelativeConstraints below(Component component){
		updateYAlignment(5, component);
		return this;
	}

	public RelativeConstraints centerVertically(){
		yAlignment = 6;
		yAlignment1 = -1;
		return this;
	}

	public RelativeConstraints centerHorizontally(){
		xAlignment = 6;
		xAlignment1 = -1;
		return this;
	}

	public RelativeConstraints centerInParent(){
		xAlignment = 6;
		xAlignment1 = -1;
		yAlignment = 6;
		yAlignment1 = -1;
		return this;
	}

	public RelativeConstraints setMargins(Insets margins){
		this.margins = margins;
		return this;
	}

	public RelativeConstraints setMarginTop(int marginTop){
		margins.top = marginTop;
		return this;
	}

	public RelativeConstraints setMarginLeft(int marginLeft){
		margins.left = marginLeft;
		return this;
	}

	public RelativeConstraints setMarginRight(int marginRight){
		margins.right = marginRight;
		return this;
	}

	public RelativeConstraints setMarginBottom(int marginBottom){
		margins.bottom = marginBottom;
		return this;
	}

	private void updateXAlignment(int x, Component component){
		if(xAlignment > -1){
			xAlignment1 = xAlignment;
			xComponent1 = xComponent;
		}
		xAlignment = x;
		xComponent = component;
	}

	private void updateYAlignment(int y, Component component){
		if(yAlignment > -1){
			yAlignment1 = yAlignment;
			yComponent1 = yComponent;
		}
		yAlignment = y;
		yComponent = component;
	}
}
