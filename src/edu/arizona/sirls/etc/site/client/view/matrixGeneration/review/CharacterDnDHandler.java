package edu.arizona.sirls.etc.site.client.view.matrixGeneration.review;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.NativeEvent;

public class CharacterDnDHandler implements DnDHandler {

	private ViewImpl viewImpl;
	private int draggedCharacter = -1;
	
	
	public CharacterDnDHandler(ViewImpl viewImpl) {
		super();
		this.viewImpl = viewImpl;
	}

	@Override
	public void onDragStart(Context context, NativeEvent event) {
		this.draggedCharacter = context.getColumn() - 1;
	}

	@Override
	public void onDrop(Context context, NativeEvent event) {
		if(draggedCharacter != -1)
			viewImpl.moveCharacter(draggedCharacter, context.getColumn() - 1);
		draggedCharacter = -1;
	}

}
