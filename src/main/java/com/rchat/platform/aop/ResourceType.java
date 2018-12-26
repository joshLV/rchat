package com.rchat.platform.aop;

/**
 * 资源类型，对应数据库 t_resource，新增的资源要在这里添加类型
 *
 * @author dzhang
 * @since 2017-02-24 16:28:25
 */
public enum ResourceType {
	UNKNOW {
		@Override
		public String toString() {
			return "未知资源";
		}

		@Override
		public String code() {
			return null;
		}
	},
	NONE {
		@Override
		public String toString() {
			return "任意资源";
		}

		@Override
		public String code() {
			return "0";
		}
	},
	/**
	 * 集团
	 */
	GROUP {
		@Override
		public String toString() {
			return "集团";
		}

		@Override
		public String code() {
			return "2e43ebbb-fc06-4dc6-944b-40b336c9141e";
		}
	},
	/**
	 * 代理商
	 */
	AGENT {
		@Override
		public String toString() {
			return "代理商";
		}

		@Override
		public String code() {
			return "26a7d196-6048-433f-92bb-11e186497d1f";
		}
	},
	TERMINAL_AGENT {
		@Override
		public String toString() {
			return "终端代理商";
		}

		@Override
		public String code() {
			return "fa9ac232-2a52-43ec-9c74-d05e98a720d9";
		}

	},
	COMPANY {
		@Override
		public String toString() {
			return "公司";
		}

		@Override
		public String code() {
			return "93cb8548-b9f1-4236-8b56-17166bf7acbd";
		}

	},
	DEPARTMENT {
		@Override
		public String toString() {
			return "部门";
		}

		@Override
		public String code() {
			return "530d2e1b-2f1d-4d47-9762-7268334210b6";
		}

	},
	TALKBACK_USER {
		@Override
		public String toString() {
			return "对讲用户";
		}

		@Override
		public String code() {
			return "6620873f-620a-4798-a63a-5acc926586a2";
		}

	},
	USER {
		@Override
		public String toString() {
			return "用户信息";
		}

		@Override
		public String code() {
			return "ad38d0cd-8d1b-4a6f-be19-107a87d62b5b";
		}
	},
	ROLE {
		@Override
		public String toString() {
			return "角色";
		}

		@Override
		public String code() {
			return "36b82a14-a922-4380-a35d-da4a915b697e";
		}
	},
	Segment {
		@Override
		public String toString() {
			return "号段";
		}

		@Override
		public String code() {
			return "057adc1a-90cb-4e68-8a37-f856515ba711";
		}

	},
	CREDIT_ACCOUNT {
		@Override
		public String toString() {
			return "额度账户";
		}

		@Override
		public String code() {
			return "fa2ebb4b-d3fc-4130-87ea-59d832bcbb76";
		}
	},
	CREDIT_ORDER {
		@Override
		public String toString() {
			return "额度订单";
		}

		@Override
		public String code() {
			return "53fd2c22-77f5-42a3-a820-348100d19d2d";
		}

	},
	TALKBACK_GROUP {
		@Override
		public String toString() {
			return "对讲群组";
		}

		@Override
		public String code() {
			return "fca3a1c5-f729-4751-a389-bbebadcf74b8";
		}
	},
	SERVICE {
		@Override
		public String toString() {
			return "业务配置";
		}

		@Override
		public String code() {
			return "102af2de-0976-491b-b673-793049434f4d";
		}

	},
	AUTHORITY {
		@Override
		public String toString() {
			return "授权";
		}

		@Override
		public String code() {
			return "eecda0d5-2860-4525-9796-a7c51b318dc2";
		}

	},
	RESOURCE {
		@Override
		public String toString() {
			return "安全资源";
		}

		@Override
		public String code() {
			return "20c62709-1730-4b66-a78a-58f8f9b4106a";
		}
	},
	RESOURCE_GROUP {
		@Override
		public String toString() {
			return "资源组";
		}

		@Override
		public String code() {
			return "d3dc9448-e830-4488-9c1a-2e5edb31a0b7";
		}
	}, RENEW_LOG {
		@Override
		public String toString() {
			return "续费日志";
		}

		@Override
		public String code() {
			return "97d45310-bfbc-4fef-88ad-ac2cf6f4a9d7";
		}
	};
	/**
	 * 资源代号，与数据库 t_resource#id相对应
	 * 
	 */
	public abstract String code();
}
