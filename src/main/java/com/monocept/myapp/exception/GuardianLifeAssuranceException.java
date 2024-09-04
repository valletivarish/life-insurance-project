package com.monocept.myapp.exception;

public class GuardianLifeAssuranceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public GuardianLifeAssuranceException(String message) {
		super(message);
	}

	public static class ResourceNotFoundException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public ResourceNotFoundException(String message) {
			super(message);
		}
	}
	public static class ResourceAlreadyDeactivedException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public ResourceAlreadyDeactivedException(String message) {
			super(message);
		}
	}
	public static class ResourceNotActiveException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public ResourceNotActiveException(String message) {
			super(message);
		}
	}
	public static class DocumentNotVerifiedException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public DocumentNotVerifiedException(String message) {
			super(message);
		}
	}


	public static class UserNotFoundException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public UserNotFoundException(String message) {
			super(message);
		}
	}

	public static class UserAlreadyActivatedException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public UserAlreadyActivatedException(String message) {
			super(message);
		}
	}

	public static class UserAlreadyDeActivatedException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public UserAlreadyDeActivatedException(String message) {
			super(message);
		}
	}

	public static class UserNotActiveException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public UserNotActiveException(String message) {
			super(message);
		}
	}

	public static class SchemeNotActiveException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public SchemeNotActiveException(String message) {
			super(message);
		}
	}

	public static class PlanNotActiveException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public PlanNotActiveException(String message) {
			super(message);
		}
	}

	public static class SchemeNotFoundException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public SchemeNotFoundException(String message) {
			super(message);
		}
	}

	public static class PlanNotFoundException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public PlanNotFoundException(String message) {
			super(message);
		}
	}

	public static class NoUserFoundException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public NoUserFoundException(String message) {
			super(message);
		}
	}

	public static class NoTransactionsFoundException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public NoTransactionsFoundException(String message) {
			super(message);
		}
	}

	public static class UserNotAssociatedException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public UserNotAssociatedException(String message) {
			super(message);
		}
	}

	public static class AccountNotActiveException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public AccountNotActiveException(String message) {
			super(message);
		}
	}

	public static class AccountDoesNotBelongToUserException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public AccountDoesNotBelongToUserException(String message) {
			super(message);
		}
	}

	public static class AccountAlreadyActiveException extends GuardianLifeAssuranceException {
		private static final long serialVersionUID = 1L;

		public AccountAlreadyActiveException(String message) {
			super(message);
		}
	}

}
