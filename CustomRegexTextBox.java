package com.fgmwallet.client.view.components;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fgmwallet.client.resources.EsewaResources;
import com.fgmwallet.client.view.validation.ValidatingTextBox;
import com.fgmwallet.client.view.validation.Validation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasCaption;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CustomRegexTextBox extends Composite implements HasCaption, HasKeyUpHandlers, HasBlurHandlers {

	private static CustomRegexTextBoxUiBinder uiBinder = GWT.create(CustomRegexTextBoxUiBinder.class);

	private static Logger rootLogger = Logger.getLogger("REGEX");

	interface CustomRegexTextBoxUiBinder extends UiBinder<Widget, CustomRegexTextBox> {
	}

	public class Settings {

		private String placeHolder;

		public Settings() {

		}

		public Settings(String placeHolder) {
			this.placeHolder = placeHolder;
		}

		public String getPlaceHolder() {
			return placeHolder;
		}

		public void setPlaceHolder(String placeHolder) {
			this.placeHolder = placeHolder;
		}

	}

	@UiField
	FlowPanel panel;
	@UiField
	Label label;
	@UiField
	ValidatingTextBox input;
	@UiField
	HTML error;

	private boolean validation = false;
	private boolean required = false;
	private boolean alpha = false;
	private boolean numeric = false;
	private boolean alphanumeric = false;
	public String title;
	private String regex = "";
	private String errorMessage = "";
	private boolean isEditable = true;
	private boolean isReadOnly = false;

	private Settings settings;
	private String mask;
	private int len;
	private String[] buffer;
	private String[] tests;
	private int cursorBegin;
	private int cursorEnd;
	private int cursorPos;
	private static final Map<String, String> defs;

	public CustomRegexTextBox() {
		initWidget(uiBinder.createAndBindUi(this));
		input.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (mask != null) {
					onKeyDownEvent(event);
				}
			}
		});

		input.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (mask != null) {
					onKeyPressEvent(event);
				}
			}
		});
	}

	public void setInputWidth(String width) {
		input.setWidth(width);
	}

	@Override
	public String getCaption() {
		return label.getText();
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return input.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return input.addKeyUpHandler(handler);
	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return input.addChangeHandler(handler);
	}

	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return input.addKeyDownHandler(handler);
	}

	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return input.addKeyPressHandler(handler);
	}

	//FORM ITEM TEXT BOX

	@Override
	public void setCaption(String text) {
		label.setText(text);
	}

	/**
	 * Perform Validation for given attribute
	 * 
	 * @return
	 */
	public boolean validate() {
		boolean valid = true;
		if (validation) {
			input.setRequired(required);
			input.setAlpha(alpha);
			input.setNumeric(numeric);
			input.setAlphaNumeric(alphanumeric);
			valid = input.validate().equals(Validation.VALID) && valid;
			error.setText(input.getErrorMessage());
		}
		return valid;
	}

	/**
	 * Perform email validation
	 * 
	 * @return
	 */
	public boolean validateAsEmail() {
		boolean valid = true;
		if (validation) {
			input.setRequired(required);
			valid = input.validateEmail().equals(Validation.VALID) && valid;
			error.setText(input.getErrorMessage());
		}
		return valid;
	}

	public boolean validateAsMobileNumber() {
		boolean valid = true;
		if (validation) {
			input.setRequired(required);
			valid = input.validateMobileNo().equals(Validation.VALID) && valid;
			error.setText(input.getErrorMessage());
		}
		return valid;
	}

	public boolean validateUsername() {
		boolean valid = (validateAsEmail() || validateAsMobileNumber());
		if (!valid && !input.getText().isEmpty()) {
			error.setText("Invalid Fgmwallet id format.");
		}
		return valid;
	}

	public boolean validateWithRegex(String regex, String message) {
		boolean valid = true;
		if (validation) {
			input.setRequired(required);
			valid = input.validateWithRegex(regex, message).equals(Validation.VALID) && valid;
			error.setText(input.getErrorMessage());
		}
		return valid;
	}

	/**
	 * Get item value
	 * 
	 * @return
	 */
	public String getText() {
		return input.getText();
	}

	/**
	 * Set value to item
	 * 
	 * @param text
	 */
	public void setText(String text) {
		if (text == null || text.length() == 0) {
			input.setText(null);
		} else {
			input.setText(text.trim());
		}
	}

	public String getErrorText() {
		return error.getText().trim();
	}

	public void setErrorText(String text) {
		input.addStyleName(EsewaResources.INSTANCE.esewaCss().error());
		error.setText(text.trim());
	}

	/**
	 * Set parameter validate as true or false
	 * 
	 * @param value
	 */
	public void setValidation(boolean value) {
		validation = value;
	}

	/**
	 * Set parameter required as true or false
	 * 
	 * @param value
	 */
	public void setRequired(boolean value) {
		required = value;
		//requiredSymbol.setVisible(true);
		//addRequiredSymbol();
	}

	/**
	 * Set parameter alphabetic as true or false
	 * 
	 * @param value
	 */
	public void setAlphabetic(boolean value) {
		alpha = value;
	}

	public void setAlphaNumeric(boolean value) {
		alphanumeric = value;
	}

	/**
	 * Set parameter numeric as true or false
	 * 
	 * @param value
	 */
	public void setNumeric(boolean value) {
		numeric = value;
	}

	/**
	 * Set parameter maxLength value
	 * 
	 * @param value
	 */
	public void setMaximumLength(int length) {
		input.setMaxLength(length);
	}

	/**
	 * Set parameter minLength value
	 * 
	 * @param value
	 */
	public void setMinimumLength(int length) {
		input.setMinLength(length);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		input.setTitle(title);
	}

	/**
	 * Set style for form panel and input box
	 * 
	 * @param panelStyle
	 *            : Apply panel style
	 * @param inputStyle
	 *            : Apply input box style
	 */
	public void setStyle(String panelStyle, String inputStyle) {
		panel.setStyleName(panelStyle);
		input.setStyleName(inputStyle);
	}

	public void setNoLabel(String panelStyle, String inputStyle) {
		label.setVisible(false);
		panel.setStyleName(panelStyle);
		input.setStyleName(inputStyle);
	}

	/**
	 * 
	 * @param style
	 */
	public void setPanelStyle(String style) {
		panel.setStyleName(style);
	}

	/**
	 * 
	 * @param style
	 */
	public void setItemStyle(String style) {
		input.setStyleName(style);
	}

	public void setErrorStyle(String style) {
		error.setStyleName(style);
	}

	public void setLabelStyle(String style) {
		label.setStyleName(style);
	}

	public void setErrorStyle() {
		error.setStyleName(EsewaResources.INSTANCE.esewaCss().errorMessage());
	}

	public void removeInvalid() {
		error.setText("");
		input.removeInvalid();
	}

	public void removeLabel() {
		label.removeFromParent();
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		input.setEnabled(isEditable);
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
		input.setReadOnly(isReadOnly);
	}

	public void clear() {
		error.setText("");
		setText("");
		input.removeInvalid();
	}

	public void validateOnBlur() {
		boolean valid = true;
		if (regex != null && !regex.isEmpty() && validation) {
			input.setRequired(required);
			input.setRegex(regex);
			input.setSpecificErrorMessage(errorMessage);
			valid = input.validateOnBlur().equals(Validation.VALID) && valid;
			error.setText(input.getErrorMessage());
		}
		if (regex == null || regex.isEmpty()) {
			validate();
		}
		if (input.getText().isEmpty() && !required) {
			error.setText("");
			input.removeInvalid();
		}
	}

	public int getCursorPos() {
		return input.getCursorPos();
	}

	/**
	 * set focus
	 * 
	 * @param focused
	 */
	public void setFocus(boolean focused) {
		input.setFocus(focused);
	}

	public void addRequiredSymbol() {
		label.setText(label.getText() + " (*)");
	}

	//CUSTOM REGEX

	static {
		defs = new HashMap<String, String>();
		defs.put("9", "[0-9]");
		defs.put("a", "[A-Za-z]");
		defs.put("*", "[A-Za-z0-9]");
	}

	private static boolean cotainDef(String key) {
		if (defs.get(key) != null) {
			return true;
		} else {
			return false;
		}
	}

	private static String getDef(String key) {
		return defs.get(key);
	}

	public void setMask(String regex) {
		clearAll();
		mask = regex;
		if (mask != null) {
			maskField();
		}
	}

	private void maskField() {
		if (mask != null) {
			input.setMaxLength(mask.length());
			settings = new Settings("_");
			tests = new String[] {};
			len = mask.length();
			tests = split(mask);
			len = mask.length();
			cursorEnd = mask.length();
			each();
			buffer();
			for (int i = cursorBegin; i < cursorEnd; i++) {
				if (tests[i] != null) {
					cursorBegin = i;
					break;
				}
			}
			writeBuffer();
			input.setCursorPos(cursorBegin);
		}
	}

	private void clearAll() {
		cursorBegin = 0;
		cursorEnd = 0;
		cursorPos = 0;
		len = 0;
		mask = null;
		buffer = new String[] {};
	}

	private String[] split(String text) {
		int length = text.length();
		String[] array = new String[length];
		for (int i = 0; i < length; i++) {
			array[i] = String.valueOf(text.charAt(i));
		}
		return array;
	}

	private void each() {
		for (int i = 0; i < tests.length; i++) {
			String c = tests[i];
			if (cotainDef(c)) {
				tests[i] = getDef(c);
			} else {
				tests[i] = null;
			}
		}
	}

	private void buffer() {
		String[] aux = split(mask);
		buffer = new String[aux.length];
		for (int i = 0; i < aux.length; i++) {
			if (cotainDef(aux[i])) {
				buffer[i] = settings.getPlaceHolder();
			} else {
				buffer[i] = aux[i];
			}
		}
	}

	private void writeBuffer() {
		String valueAux = "";
		for (String element2 : buffer) {
			valueAux += element2;
		}
		input.setValue(valueAux);
	}

	private void onKeyDownEvent(KeyDownEvent fe) {
		int k = fe.getNativeKeyCode();
		cursorPos = input.getCursorPos();
		if (k == 8) {
			fe.preventDefault();
			if (cursorPos <= cursorEnd && cursorPos > cursorBegin) {
				cursorPos = cursorPos - 1;
				if (tests[cursorPos] != null) {
					shiftL();
				} else {
					input.setCursorPos(cursorPos);
				}
				return;
			}
		}
		if (k == 46) {
			fe.preventDefault();
			if (cursorPos < cursorEnd && cursorPos >= cursorBegin) {
				if (tests[cursorPos] != null) {
					shiftL();
				}
				return;
			}
		}
		if (k == 27) {
			fe.stopPropagation();
			return;
		}
	}

	private void onKeyPressEvent(KeyPressEvent fe) {
		int k = fe.getUnicodeCharCode();
		cursorPos = input.getCursorPos();
		if (k == 8) {
			fe.stopPropagation();
			return;
		}
		if (fe.isControlKeyDown() || fe.isAltKeyDown()) {
			fe.stopPropagation();
			return;
		}
		if (k >= 32 && k <= 125 || k > 186) {
			if (k == 39) {
				fe.preventDefault();
				if (cursorPos < cursorEnd) {
					cursorPos = cursorPos + 1;
					input.setCursorPos(cursorPos);
					return;
				}
			}
			if (k == 37) {
				fe.preventDefault();
				if (cursorPos <= cursorEnd && cursorPos > cursorBegin) {
					cursorPos = cursorPos - 1;
					input.setCursorPos(cursorPos);
					return;
				}
			} else {
				fe.preventDefault();
				if (cursorPos < cursorEnd) {
					String c = String.valueOf((char) fe.getUnicodeCharCode());
					shiftR(c);
					return;
				}
			}
		}
	}

	private void shiftR(String c) {
		int index = cursorPos;
		for (int i = index; i < cursorEnd; i++) {
			if (tests[cursorPos] != null) {
				index = i;
				break;
			}
		}
		if (tests[index] != null) {
			if (c.matches(tests[index])) {
				buffer[index] = c;
				writeBuffer();
				int j = seekNext(index);
				cursorPos = j;
				input.setCursorPos(cursorPos);
			}
		}
	}

	private void shiftL() {
		int index = cursorPos;
		for (int i = index; i >= cursorBegin; i--) {
			if (tests[i] != null) {
				index = i;
				break;
			}
		}
		for (int i = index; i < len; i++) {
			if (tests[i] != null) {
				buffer[i] = settings.getPlaceHolder();
				int j = seekNext(i);
				if (j < len && buffer[j].matches(tests[i])) {
					buffer[i] = buffer[j];
				} else {
					break;
				}
			}
		}
		writeBuffer();
		cursorPos = index;
		input.setCursorPos(index);
	}

	private int seekNext(int index) {
		while (++index < len) {
			try {
				if (tests[index] != null) {
					break;
				}
			} catch (Exception e) {
				break;
			}
		}
		return index;
	}
}
