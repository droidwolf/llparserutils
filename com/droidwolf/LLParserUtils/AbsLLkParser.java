package com.droidwolf.LLParserUtils;

/** LL(K)������ */
public abstract class AbsLLkParser {
	protected final CircularArray mLookAhead;

	/**
	 * ���캯��(�ڵ��øù��캯���������fillToken��ʼ���ʷ�)
	 * 
	 * @param k
	 *            ��ǰ��k���ʷ�
	 */
	protected AbsLLkParser(int k) {
		mLookAhead = new CircularArray(k);
	}

	/**
	 * ��ǰ�ƽ�k���ʷ�
	 * 
	 * @param k
	 */
	protected void fillToken(int k) {
		for (int i = 0; i < k; i++) {
			consume();
		}
	}

	protected void consume() {
		mLookAhead.add(nextToken());
	}

	protected TokenBase LT(int i) {
		return mLookAhead.get(i);
	}

	protected short LA(int i) {
		return LT(i).getType();
	}

	protected void match(short type) throws MatchFailedException {
		if (LA(1) != type) {
			final TokenBase tok = LT(1);
			final String desc = String.format("%s mismatch %s��%s",getTokenName(type), getTokenName(tok.getType()),tok.toString());
			throw new MatchFailedException(desc);
		}
		consume();
	}

	public abstract TokenBase nextToken();

	public abstract String getTokenName(short type);

	private static class CircularArray {
		private final TokenBase[] mData;
		private int mPointer = 0;

		public CircularArray(int size) {
			mData = new TokenBase[size];
		}

		/** ��������Ԫ�ظ���>size,��pointer �ص�����ͷ */
		public void add(TokenBase val) {
			mData[mPointer] = val;
			mPointer = (mPointer + 1) % mData.length;
		}

		/** ��ȡ��i��Ԫ�أ�i>=1 */
		public TokenBase get(int i) {
			final int realP = (mPointer + i - 1) % mData.length;
			final TokenBase val = mData[realP];
			return val;
		}

		/** ��ȡ��ǰָ�� */
		public int getPointer() {
			return mPointer;
		}
	}// end class CircularArray
}// end class AbsLLkParser
