package org.mybatis.generator.internal.rules;

public class CustomRulesDelegate extends RulesDelegate {

	public CustomRulesDelegate(Rules rules) {
		super(rules);
		// TODO Auto-generated constructor stub
	}
	public boolean generateUpdateByPrimaryKeyWithBLOBs() {
		return false;
	}

	public boolean generateUpdateByPrimaryKeyWithoutBLOBs() {
		return false;
	}
	public boolean generateInsert() {
		return false;
	}
}
