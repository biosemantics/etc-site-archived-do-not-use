package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.Text;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ProvidesKey;

/**
 *
 * @author Yuri Plaksyuk
 */
public class ScrolledGrid<T> extends DataGrid<T> {

    private final Text cssText;
    private boolean addedClass = false;
    private int currentScrollLeft = 0;

    public ScrolledGrid(int pageSize, DataGrid.Resources resources, ProvidesKey<T> taxonKeyProvider) {
    	super(pageSize, resources, taxonKeyProvider);

        cssText = Document.get().createTextNode("");

        StyleElement styleElement = Document.get().createStyleElement();
        styleElement.setType("text/css");
        styleElement.appendChild(cssText);

        HeaderPanel headerPanel = (HeaderPanel) getWidget();
        headerPanel.getElement().insertFirst(styleElement);

        final ScrollPanel scrollPanel = (ScrollPanel) headerPanel.getContentWidget();
        scrollPanel.addScrollHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                int scrollLeft = scrollPanel.getHorizontalScrollPosition();
                if (scrollLeft != currentScrollLeft) {
                    StringBuilder css = new StringBuilder();
                    if (scrollLeft > 0) {
                        css.append(".ScrolledGrid-frozen {");
                        css.append("background-color: inherit;");
                        css.append("}");

                        css.append(".ScrolledGrid-frozen div {");
                        css.append("position: absolute;");
                        css.append("left: ").append(scrollLeft).append("px;");
                        css.append("width: ").append(getColumnWidth(getColumn(0))).append(";");
                        css.append("padding-left: 1.3em;");
                        css.append("padding-right: 0.5em;");
                        css.append("margin-top: -0.7em;");
                        css.append("white-space: nowrap;");
                        css.append("background-color: inherit;");
                        css.append("}");
                    }
                    else
                        css.append(".ScrolledGrid-frozen { }");

                    css.append("th.ScrolledGrid-frozen { background-color: white; }");

                    cssText.setData(css.toString());


                    if (!addedClass) {
                        NodeList<TableRowElement> rows;
                        TableRowElement row;
                        TableCellElement cell;

                        rows = getTableHeadElement().getRows();
                        for (int i = 0; i < rows.getLength(); ++i) {
                            row = rows.getItem(i);
                            cell = row.getCells().getItem(0);
                            cell.setInnerHTML("<div>" + cell.getInnerHTML() + "</div>");
                            cell.addClassName("ScrolledGrid-frozen");
                        }

                        rows = getTableBodyElement().getRows();
                        for (int i = 0; i < rows.getLength(); ++i) {
                            row = rows.getItem(i);
                            cell = row.getCells().getItem(0);

                            cell.addClassName("ScrolledGrid-frozen");
                        }
                        addedClass = true;
                    }

                    currentScrollLeft = scrollLeft;
                }
            }
        });
    }
}