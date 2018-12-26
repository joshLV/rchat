package com.rchat.platform.aop;

/**
 * 操作类型，包括增删改查：CRUD
 *
 * @author dzhang
 * @since 2017-02-24 16:20:55
 */
public enum OperationType {
	/**
	 * 全部操作，当作是什么都可以，不需要验证
	 */
	ALL {
		@Override
		public int code() {
			return OperationType.CREATE.code() | OperationType.DELETE.code() | UPDATE.code()
					| OperationType.RETRIEVE.code();
		}

		@Override
		public String toString() {
			return "全权";
		}
	},
	/**
	 * 没有操作
	 */
	NONE {
		@Override
		public int code() {
			return 0;
		}

		@Override
		public String toString() {
			return "无权";
		}
	},
	/**
	 * 创建操作
	 */
	CREATE {
		@Override
		public int code() {
			return 1 << 3;
		}

		@Override
		public String toString() {
			return "创建";
		}
	},
	/**
	 * 删除操作
	 */
	DELETE {
		@Override
		public int code() {
			return 1 << 2;
		}

		@Override
		public String toString() {
			return "删除";
		}
	},
	/**
	 * 修改操作
	 */
	UPDATE {
		@Override
		public int code() {
			return 1 << 1;
		}

		@Override
		public String toString() {
			return "更新";
		}
	},
	/**
	 * 查询操作
	 */
	RETRIEVE {
		@Override
		public int code() {
			return 1 << 0;
		}

		@Override
		public String toString() {
			return "查询";
		}
	};
	/**
	 * 操作编码
	 * 
	 * @return
	 */
	public abstract int code();
}
