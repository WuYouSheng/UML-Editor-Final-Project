package mod.instance;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;  // 新增 Graphics2D 類別
import java.awt.BasicStroke; // 新增 BasicStroke 類別
import java.awt.Point;
import java.awt.Polygon;

import javax.swing.JPanel;

import Define.AreaDefine;
import Pack.DragPack;
import bgWork.handler.CanvasPanelHandler;
import mod.IFuncComponent;
import mod.ILinePainter;
import java.lang.Math;

public class DependencyLine extends JPanel
        implements IFuncComponent, ILinePainter
{
    JPanel				from;
    JPanel				to;
    int					fromSide;
    int					toSide;
    Point				fp				= new Point(0, 0);
    Point				tp				= new Point(0, 0);
    int					arrowSize		= 6;
    int					panelExtendSize	= 10;
    boolean				isSelect		= false;
    int					selectBoxSize	= 5;
    CanvasPanelHandler	cph;
    float[]				dashPattern		= {5.0f, 5.0f};  // 虛線樣式的模式，5個像素實線，5個像素空白

    public DependencyLine(CanvasPanelHandler cph)
    {
        this.setOpaque(false);
        this.setVisible(true);
        this.setMinimumSize(new Dimension(1, 1));
        this.cph = cph;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Point fpPrime;
        Point tpPrime;
        renewConnect();
        fpPrime = new Point(fp.x - this.getLocation().x, fp.y - this.getLocation().y);
        tpPrime = new Point(tp.x - this.getLocation().x, tp.y - this.getLocation().y);

        // 轉換為 Graphics2D 以使用進階繪圖功能
        Graphics2D g2d = (Graphics2D) g;

        // 儲存原始筆觸
        BasicStroke originalStroke = (BasicStroke) g2d.getStroke();

        // 設定虛線筆觸
        BasicStroke dashedStroke = new BasicStroke(
                1.0f,            // 線條寬度
                BasicStroke.CAP_BUTT,  // 線端樣式
                BasicStroke.JOIN_MITER,// 線條連接處樣式
                10.0f,                 // 連接處限制
                dashPattern,           // 虛線模式
                0.0f                   // 相位偏移
        );

        g2d.setStroke(dashedStroke);
        g2d.setColor(Color.BLACK);
        g2d.drawLine(fpPrime.x, fpPrime.y, tpPrime.x, tpPrime.y);

        // 繪製箭頭前恢復實線筆觸（箭頭邊緣應為實線）
        g2d.setStroke(originalStroke);
        paintArrow(g2d, tpPrime);

        if (isSelect == true)
        {
            paintSelect(g2d);
        }
    }

    @Override
    public void reSize()
    {
        Dimension size = new Dimension(
                Math.abs(fp.x - tp.x) + panelExtendSize * 2,
                Math.abs(fp.y - tp.y) + panelExtendSize * 2);
        this.setSize(size);
        this.setLocation(Math.min(fp.x, tp.x) - panelExtendSize,
                Math.min(fp.y, tp.y) - panelExtendSize);
    }

    @Override
    public void paintArrow(Graphics g, Point point)
    {
        int x[] =
                {point.x, point.x - arrowSize, point.x, point.x + arrowSize};
        int y[] =
                {point.y + arrowSize, point.y, point.y - arrowSize, point.y};
        switch (toSide)
        {
            case 0:
                x = removeAt(x, 0);
                y = removeAt(y, 0);
                break;
            case 1:
                x = removeAt(x, 1);
                y = removeAt(y, 1);
                break;
            case 2:
                x = removeAt(x, 3);
                y = removeAt(y, 3);
                break;
            case 3:
                x = removeAt(x, 2);
                y = removeAt(y, 2);
                break;
            default:
                break;
        }
        Polygon polygon = new Polygon(x, y, x.length);
        g.setColor(Color.WHITE);
        g.fillPolygon(polygon);
        g.setColor(Color.BLACK);
        g.drawPolygon(polygon);
    }

    @Override
    public void setConnect(DragPack dPack)
    {
        Point mfp = dPack.getFrom();//來源座標
        Point mtp = dPack.getTo();//目的座標
        from = (JPanel) dPack.getFromObj();//來源物件
        to = (JPanel) dPack.getToObj();//目的物件
        fromSide = new AreaDefine().getArea(from.getLocation(), from.getSize(), mfp);//來源邊
        toSide = new AreaDefine().getArea(to.getLocation(), to.getSize(), mtp);//目的邊
        renewConnect();
        System.out.println("from side " + fromSide);
        System.out.println("to side " + toSide);
    }

    void renewConnect()
    {
        try
        {
            fp = getConnectPoint(from, fromSide);
            tp = getConnectPoint(to, toSide);
            this.reSize();
        }
        catch (NullPointerException e)
        {
            this.setVisible(false);
            cph.removeComponent(this);
        }
    }

    Point getConnectPoint(JPanel jp, int side)
    {
        Point temp = new Point(0, 0);
        Point jpLocation = cph.getAbsLocation(jp);
        if (side == new AreaDefine().TOP)
        {
            temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
            temp.y = jpLocation.y;
        }
        else if (side == new AreaDefine().RIGHT)
        {
            temp.x = (int) (jpLocation.x + jp.getSize().getWidth());
            temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
        }
        else if (side == new AreaDefine().LEFT)
        {
            temp.x = jpLocation.x;
            temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
        }
        else if (side == new AreaDefine().BOTTOM)
        {
            temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
            temp.y = (int) (jpLocation.y + jp.getSize().getHeight());
        }
        else
        {
            temp = null;
            System.err.println("getConnectPoint fail:" + side);
        }
        return temp;
    }

    int[] removeAt(int arr[], int index)
    {
        int temp[] = new int[arr.length - 1];
        for (int i = 0; i < temp.length; i ++)
        {
            if (i < index)
            {
                temp[i] = arr[i];
            }
            else if (i >= index)
            {
                temp[i] = arr[i + 1];
            }
        }
        return temp;
    }

    @Override
    public void paintSelect(Graphics gra)
    {
        gra.setColor(Color.BLACK);
        gra.fillRect(fp.x, fp.y, selectBoxSize, selectBoxSize);
        gra.fillRect(tp.x, tp.y, selectBoxSize, selectBoxSize);
    }

    public boolean isSelect()
    {
        return isSelect;
    }

    public void setSelect(boolean isSelect)
    {
        this.isSelect = isSelect;
    }
}