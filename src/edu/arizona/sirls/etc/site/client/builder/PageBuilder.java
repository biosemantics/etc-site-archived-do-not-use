package edu.arizona.sirls.etc.site.client.builder;

import com.google.gwt.user.client.DOM;

public class PageBuilder {

	private IFooterBuilder footerBuilder;
	private IContentBuilder contentBuilder;
	private IHeaderBuilder headerBuilder;
	private IMenuBuilder menuBuilder;
	private boolean footerBuilderChanged = true;
	private boolean contentBuilderChanged = true;
	private boolean headerBuilderChanged = true;
	private boolean menuBuilderChanged = true;

	public PageBuilder(IFooterBuilder footerBuilder, IContentBuilder contentBuilder, IHeaderBuilder headerBuilder, IMenuBuilder menuBuilder) {
		this.footerBuilder = footerBuilder;
		this.contentBuilder = contentBuilder;
		this.headerBuilder = headerBuilder;
		this.menuBuilder = menuBuilder;
	}

	public void build() {
		if(headerBuilderChanged) {
			cleanElement("header");
			this.headerBuilder.build();
		}
		if(menuBuilderChanged) {
			cleanElement("menu");
			this.menuBuilder.build();
		}
		if(contentBuilderChanged) {
			cleanElement("content");
			this.contentBuilder.build();
		}
		if(footerBuilderChanged) {
			cleanElement("footer");
			this.footerBuilder.build();
		}
	}

	private void cleanElement(String string) {
		DOM.getElementById(string).setInnerHTML("");
	}

	public IFooterBuilder getFooterBuilder() {
		return footerBuilder;
	}

	public void setFooterBuilder(IFooterBuilder footerBuilder) {
		this.footerBuilder = footerBuilder;
		footerBuilderChanged = true;
	}

	public IContentBuilder getContentBuilder() {
		return contentBuilder;
	}

	public void setContentBuilder(IContentBuilder contentBuilder) {
		this.contentBuilder = contentBuilder;
		contentBuilderChanged = true;
	}

	public IHeaderBuilder getHeaderBuilder() {
		return headerBuilder;
	}

	public void setHeaderBuilder(IHeaderBuilder headerBuilder) {
		this.headerBuilder = headerBuilder;
		headerBuilderChanged = true;
	}

	public IMenuBuilder getMenuBuilder() {
		return menuBuilder;
	}

	public void setMenuBuilder(IMenuBuilder menuBuilder) {
		this.menuBuilder = menuBuilder;
		menuBuilderChanged = true;
	}
	
	
}
