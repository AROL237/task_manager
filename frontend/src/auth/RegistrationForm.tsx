import { useState, type FormEvent } from "react";
import { fetchApi } from "@/lib/api";
import type ApiResponse from "./model/ApiResponse";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Field, FieldError, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";

type RegistrationFormProps = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess: (message: string) => void;
};

type RegistrationPayload = {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  gender: "M" | "F" | "";
};

type RegistrationErrors = Partial<
  Record<keyof RegistrationPayload | "confirmEmail", string>
>;

const initialPayload: RegistrationPayload = {
  firstName: "",
  lastName: "",
  email: "",
  password: "",
  gender: "",
};

export default function RegistrationForm({
  open,
  onOpenChange,
  onSuccess,
}: RegistrationFormProps) {
  const [payload, setPayload] = useState(initialPayload);
  const [confirmEmail, setConfirmEmail] = useState("");
  const [errors, setErrors] = useState<RegistrationErrors>({});
  const [formError, setFormError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);

  function resetForm() {
    setPayload(initialPayload);
    setConfirmEmail("");
    setErrors({});
    setFormError("");
    setSuccessMessage("");
  }

  function closeForm() {
    if (!submitting) {
      resetForm();
      onOpenChange(false);
    }
  }

  function validate(): RegistrationErrors {
    const nextErrors: RegistrationErrors = {};
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!payload.firstName.trim())
      nextErrors.firstName = "First name is required.";
    if (!payload.lastName.trim())
      nextErrors.lastName = "Last name is required.";
    if (!emailPattern.test(payload.email.trim()))
      nextErrors.email = "Enter a valid email address.";
    if (payload.email.trim() !== confirmEmail.trim()) {
      nextErrors.confirmEmail = "Email addresses must match.";
    }
    if (payload.password.length < 6) {
      nextErrors.password = "Password must be at least 6 characters.";
    }
    if (!payload.gender) nextErrors.gender = "Select a gender.";

    return nextErrors;
  }

  async function register(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFormError("");
    setSuccessMessage("");

    const nextErrors = validate();
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    setSubmitting(true);
    try {
      const response = await fetchApi<ApiResponse>("/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...payload,
          firstName: payload.firstName.trim(),
          lastName: payload.lastName.trim(),
          email: payload.email.trim(),
        }),
      });

      if (
        response.success === false ||
        ![200, 201].includes(Number(response.code))
      ) {
        setFormError(
          response.message ? String(response.message) : "Registration failed.",
        );
        return;
      }

      const createdUser = response.data;
      if (createdUser == null) {
        setFormError("Registration completed without user data.");
        return;
      }

      const message = response.message
        ? String(response.message)
        : "Registration successful.";

      setSuccessMessage(message);
      setErrors({});
      onSuccess(message);
      resetForm();
      onOpenChange(false);
    } catch (error) {
      setFormError(
        error instanceof Error ? error.message : "Registration failed.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90vh] overflow-y-auto">
        <DialogClose onClick={closeForm} />
        <DialogHeader>
          <DialogTitle>Create an account</DialogTitle>
          <DialogDescription>Enter your details to register.</DialogDescription>
        </DialogHeader>
        <form className="flex flex-col gap-4" onSubmit={register} noValidate>
          <div className="grid gap-4 sm:grid-cols-2">
            <Field>
              <FieldLabel htmlFor="firstName">First name</FieldLabel>
              <Input
                id="firstName"
                name="firstName"
                value={payload.firstName}
                aria-invalid={Boolean(errors.firstName)}
                onChange={(event) =>
                  setPayload({ ...payload, firstName: event.target.value })
                }
              />
              <FieldError>{errors.firstName}</FieldError>
            </Field>
            <Field>
              <FieldLabel htmlFor="lastName">Last name</FieldLabel>
              <Input
                id="lastName"
                name="lastName"
                value={payload.lastName}
                aria-invalid={Boolean(errors.lastName)}
                onChange={(event) =>
                  setPayload({ ...payload, lastName: event.target.value })
                }
              />
              <FieldError>{errors.lastName}</FieldError>
            </Field>
          </div>
          <Field>
            <FieldLabel htmlFor="register-email">Email</FieldLabel>
            <Input
              id="register-email"
              name="email"
              type="email"
              value={payload.email}
              aria-invalid={Boolean(errors.email)}
              onChange={(event) =>
                setPayload({ ...payload, email: event.target.value })
              }
            />
            <FieldError>{errors.email}</FieldError>
          </Field>
          <Field>
            <FieldLabel htmlFor="confirmEmail">Confirm email</FieldLabel>
            <Input
              id="confirmEmail"
              name="confirmEmail"
              type="email"
              value={confirmEmail}
              aria-invalid={Boolean(errors.confirmEmail)}
              onChange={(event) => setConfirmEmail(event.target.value)}
            />
            <FieldError>{errors.confirmEmail}</FieldError>
          </Field>
          <Field>
            <FieldLabel htmlFor="register-password">Password</FieldLabel>
            <Input
              id="register-password"
              name="password"
              type="password"
              minLength={6}
              value={payload.password}
              aria-invalid={Boolean(errors.password)}
              onChange={(event) =>
                setPayload({ ...payload, password: event.target.value })
              }
            />
            <FieldError>{errors.password}</FieldError>
          </Field>
          <Field>
            <FieldLabel>Gender</FieldLabel>
            <div className="flex gap-5" role="radiogroup" aria-label="Gender">
              {(["M", "F"] as const).map((gender) => (
                <label className="flex items-center gap-2" key={gender}>
                  <input
                    type="radio"
                    name="gender"
                    value={gender}
                    checked={payload.gender === gender}
                    onChange={() => setPayload({ ...payload, gender })}
                  />
                  {gender === "M" ? "Male" : "Female"}
                </label>
              ))}
            </div>
            <FieldError>{errors.gender}</FieldError>
          </Field>
          {formError && <FieldError>{formError}</FieldError>}
          {successMessage && (
            <p className="text-sm text-green-600">{successMessage}</p>
          )}
          <div className="flex justify-end gap-2">
            <Button
              type="button"
              variant="outline"
              onClick={closeForm}
              disabled={submitting}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? "Registering..." : "Register"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
