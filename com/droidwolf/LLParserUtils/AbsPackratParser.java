package com.droidwolf.LLParserUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �������ݽ�����
 */
public abstract class AbsPackratParser {
	protected final static boolean DBG = true;
	protected final static int PARESED_FAILED = -1;

	/** ��ǰ���ʷ� */
	protected final List<TokenBase> mLookAhead = new ArrayList<TokenBase>();

	/** ���ݴʷ�ָ��ջ */
	protected final ArrayDeque<Integer> mBacktraceToken = new ArrayDeque<Integer>();

	/** ��ʼ�ʷ�ָ�룬�����ʷ�ָ�루PARESED_FAILED����ʧ�ܣ� */
	protected final Map<Integer, Integer> mParsedRules = new HashMap<Integer, Integer>();
	protected int mPointer = 0;

	/** �ƽ���һ�ʷ� */
	protected void consume() {
		++mPointer;
		// ���������ң��ʷ�ָ�뵽�˽�β
		if (isPointerAfterLast() && !isTestAltMatch()) {
			clearLookAheadToken();
			clearParsedRules();
		}
		ensureToken(1);
	}

	/** ȷ����ȡ��i���ʷ� */
	protected void ensureToken(int i) {
		final int p = mPointer + i /*- 1*/;
		final int tkSize = mLookAhead.size() /*- 1*/;
		if (p > tkSize) {
			final int n = p - tkSize;
			for (int j = 0; j < n; j++) {
				mLookAhead.add(nextToken());
			}
		}
	}

	protected TokenBase LT(int i) {
		ensureToken(i);
		return mLookAhead.get(mPointer + i - 1);
	}

	/** ���شʷ� type */
	protected short LA(int i) {
		return LT(i).getType();
	}
	public abstract TokenBase nextToken();

	public abstract String getTokenName(short type);
	protected void match(short type) throws MatchFailedException {
		if (LA(1) != type) {
			final TokenBase tok = LT(1);
			final String desc = String.format("%s mismatch %s��%s",getTokenName(type), getTokenName(tok.getType()),tok.toString());
			throw new MatchFailedException(desc);
		}
		consume();
	}

	/**
	 * �����Ѿ�������
	 * 
	 * @return true ������false��ǰ�ʷ�Ϊ�����������
	 * @throws PrevAltMatchFailedException
	 */
	protected boolean skipParsedSuccessRule()throws PrevAltMatchFailedException {
		final int ctk = getCurrentTokenPointer();
		Integer ret = mParsedRules.get(ctk);
		if (ret == null)
			return false;
		if (ret.intValue() == PARESED_FAILED) {
			throw new PrevAltMatchFailedException();
		}
		if (DBG) {
			final String toTkp = getTokenString(ret), fromTkp = getTokenString(ctk);
			log("skipParsedSuccessRule", String.format("skip token[%d->%d],\"%s\"->\"%s\"", ctk, ret, fromTkp,toTkp));
		}
		setCurrentTokenPointer(ret);
		return true;
	}

	/***
	 * �����Ѿ������ʷ�������
	 * 
	 * @param tkPointer
	 *            �ʷ�ָ��
	 * @param success
	 *            ƥ���Ƿ�ɹ�
	 */
	protected void saveParsedRule(int tkPointer, boolean success) {
		if (isTestAltMatch()) {
			mParsedRules.put(tkPointer, success? getCurrentTokenPointer(): PARESED_FAILED);
			if (DBG) {
				final String tk = getTokenString(tkPointer);
				log("saveParsedRule",String.format("start stk=%d, end stk=%d, success=%b,mParsedRules size=%d, %s",tkPointer, getCurrentTokenPointer(), success,mParsedRules.size(), tk));
			}
		}
	}

	/** ����ѽ������� */
	protected void clearParsedRules() {
		if (DBG) {
			log("clearParsedRules", "size=" + mParsedRules.size());
		}
		mParsedRules.clear();
	}

	/** ��ȡ��ǰ�ʷ�ָ�� */
	protected int getCurrentTokenPointer() {
		return mPointer;
	}

	/** ���õ�ǰ�ʷ�ָ�� */
	protected void setCurrentTokenPointer(int tkp) {
		mPointer = tkp;
	}

	/** �ʷ�ָ���Ƿ��Ƶ�����β�� */
	protected boolean isPointerAfterLast() {
		return mPointer == mLookAhead.size();
	}

	/** �Ƿ��ڳ��Է�֧��������� */
	protected boolean isTestAltMatch() {
		final boolean ret = !mBacktraceToken.isEmpty();
		return ret;
	}

	/** ��ǰ�ʷ�ָ����ջ */
	protected int pushTokenPointer() {
		final int tkp = getCurrentTokenPointer();
		mBacktraceToken.push(tkp);
		if (DBG) {
			final String tk = getTokenString(tkp);
			log("pushTokenPointer", "" + tkp + ", stack size="+ mBacktraceToken.size() + ", " + tk);
		}
		return tkp;
	}

	/** ����ջ���ʷ�ָ�룬������Ϊ��ǰ�ʷ�ָ�� */
	protected void popTokenPointer() {
		final int tkp = mBacktraceToken.pop();
		if (DBG) {
			final String tk = getTokenString(tkp);
			log("popTokenPointer", tkp + ", stack size=" + mBacktraceToken.size() + ", "+ tk);
		}
		setCurrentTokenPointer(tkp);
	}

	/** �����ǰ���ʷ� */
	protected void clearLookAheadToken() {
		mPointer = 0;
		mLookAhead.clear();
	}

	private String getTokenString(int tkp) {
		return tkp > 0 ? LT(tkp).toString() : "token index=" + tkp;
	}

	protected static void log(String tag, String msg) {
		System.out.println(tag + "--" + msg);
	}
}// end class
