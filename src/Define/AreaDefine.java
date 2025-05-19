package Define;

import java.awt.Dimension;
import java.awt.Point;

public class AreaDefine
{
	int					xMask		= 0x01;
	int					yMask		= 0x02;
	public final int	OUT_SIDE	= -1;
	public final int	TOP			= 3;
	public final int	RIGHT		= 2;
	public final int	LEFT		= 1;
	public final int	BOTTOM		= 0;

	public int getArea(Point rectLocat, Dimension rectSize, Point click)
	{
		if (click.x < rectLocat.x || click.y < rectLocat.y)
		{
			return OUT_SIDE;
		}
		if (click.x > rectLocat.x + rectSize.width
				|| click.y > rectLocat.y + rectSize.height)
		{
			return OUT_SIDE;
		}
		Dimension vx = new Dimension(rectSize.width, -rectSize.height);
		Dimension vy = new Dimension(rectSize.width, rectSize.height);
		Point center = new Point(rectLocat.x + rectSize.width / 2,
				rectLocat.y + rectSize.height / 2);
		Dimension vc = new Dimension(click.x - center.x, click.y - center.y);
		double x = vc.width * vx.height - vc.height * vx.width;
		double y = vc.width * vy.height - vc.height * vy.width;
		int result = 0;
		if (x >= 0.0)
		{
			result |= xMask;
		}
		if (y >= 0.0)
		{
			result |= yMask;
		}
		switch (result)
		{
			case TOP:
				return TOP;
			case RIGHT:
				return RIGHT;
			case LEFT:
				return LEFT;
			case BOTTOM:
				return BOTTOM;
			default:
				return OUT_SIDE;
		}
	}

	public Point getSideCenter(Point rectLocat, Dimension rectSize,Point click) {

		Point center = new Point(rectLocat.x + rectSize.width / 2, rectLocat.y + rectSize.height / 2);
		Point result = new Point(0, 0);

		switch (getArea(rectLocat,rectSize,click)) {
			case TOP:
				result.x = center.x;
				result.y = rectLocat.y;
				break;
			case BOTTOM:
				result.x = center.x;
				result.y = rectLocat.y + rectSize.height;
				break;
			case LEFT:
				result.x = rectLocat.x;
				result.y = center.y;
				break;
			case RIGHT:
				result.x = rectLocat.x + rectSize.width;
				result.y = center.y;
				break;
			default:
				return null; // 如果 side 無效，回傳 null
		}
		return result;
	}
}
