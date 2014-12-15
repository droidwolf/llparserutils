package com.droidwolf.LLParserUtils;

/** AbsPackratParser�򻯻��ࡣ�򻯷�֧���ԡ���֧�ɹ�ƥ���¼ */
public abstract class AbsPackratParserEx extends AbsPackratParser {
	/**
	 * ���鳢�Բ��Թ������Ƶķ�֧�����ûһ����֧ƥ�����׳�NoneAltMatchException
	 * 
	 * @param alts
	 * @throws MatchException
	 */
	protected final void groupAltMatch(MatchAlternative[] alts)
			throws MatchException {
		for (MatchAlternative ma : alts) {
			if (callTestMatch(ma)) {
				ma.normalMatch();
				return;
			}
		}
		final TokenBase tok = LT(1);
		final String desc = "unknow alternative,start with token type "+ getTokenName(tok.getType()) + "��" + tok.toString();
		throw new NoneAltMatchException(desc);
	}

	/**
	 * testMatch����ƥ��(������ƥ��)һ������
	 * 
	 * @param matchAlternative
	 * @return
	 * @throws MatchException
	 */
	protected final boolean callTestMatch(MatchAlternative matchAlternative)throws MatchException {
		boolean success = true;
		pushTokenPointer();
		try {
			matchAlternative.testMatch();
		} catch (MatchException e) {
			success = false;
		}
		popTokenPointer();
		return success;
	}

	/**
	 * ��¼���Է�֧���ظ����Թ����ƥ����
	 * 
	 * @param matchAlternative
	 * @throws MatchException
	 */
	protected final void callMatchRecord(MatchAndRecord matchAndRecord)throws MatchException {
		if (isTestAltMatch() && skipParsedSuccessRule()) {
			return;
		}
		boolean success = true;
		final int startTkp = getCurrentTokenPointer();
		try {
			matchAndRecord.matchRecord();
		} catch (MatchFailedException re) {
			success = false;
			throw re;
		}
		if (isTestAltMatch()) {
			saveParsedRule(startTkp, success);
		}
	}

	/** ��֧ƥ�� */
	public interface MatchAlternative {
		/**
		 * ���Է�֧ʱ(�Զ����桢�ָ���ջ)���ø÷�������ֱ�ӵ��ã���callTestMatch������ø÷���
		 * 
		 * @return ʵ�ַ���������
		 * @throws MatchException
		 */
		boolean testMatch() throws MatchException;

		/** ����ƥ��ʱ���� */
		void normalMatch() throws MatchException;
	}

	/** ƥ�䲢��¼ƥ���� */
	public interface MatchAndRecord {
		/** ƥ�䲢��¼ƥ��������ֱ�ӵ��ã���callMatchRecord������ø÷��� */
		void matchRecord() throws MatchException;
	}
}// end class AbsPackratParserEx
