package edu.arizona.biosemantics.etcsite.shared.help;

import java.util.HashMap;
import java.util.Map;

public class Help {

	public static enum Type {
		WELCOME, TEXT_CAPTURE_INPUT, TEXT_CAPTURE_PROCESS, MATRIX_GENERATION_INPUT, HOME; //...
		
		public String getKey() {
			return this.getClass() + "_" + this.name();
		}
	}

	private static Map<Type, String> helps = new HashMap<Type, String>();
	
	static {
		helps.put(Type.HOME, 
				"here goes the help html for home input.");
		
		helps.put(Type.TEXT_CAPTURE_INPUT, 
				"here goes the help html for semanticmarkup input. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc,");

		helps.put(Type.TEXT_CAPTURE_PROCESS, 
				"here goes the help html for semanticmarkup process");

		helps.put(Type.MATRIX_GENERATION_INPUT, 
				"here goes the help html for matrix generation input");
		
		//..
	}
	
	public static String getHelp(Type type) {
		return helps.get(type);
	}
	
	
}
