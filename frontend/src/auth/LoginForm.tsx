import { Button } from "@/components/ui/button";
import {
  Card,
  CardAction,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import Container from "@/components/ui/custom/Container";
import { Field, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import {
  Alert,
  AlertDescription,
  AlertTitle,
} from "@/components/ui/alert";
import RegistrationForm from "./RegistrationForm";

import { useEffect, useState, type ChangeEvent } from "react";
import { fetchApi } from "@/lib/api";
import type ApiResponse from "./model/ApiResponse";
import type LoginPayload from "./model/LoginPayload";

type LoginPageProps = {
  loadUser: any;
};

export default function LoginPage({ loadUser }: LoginPageProps) {
  const [registrationOpen, setRegistrationOpen] = useState(false);
  const [loginError, setLoginError] = useState("");
  const [registrationMessage, setRegistrationMessage] = useState("");
  const [payload, setPayload] = useState<LoginPayload>({
    username: "",
    password: "",
  });

  function handleChange(
    event: ChangeEvent<HTMLInputElement, HTMLInputElement>,
  ): void {
    const { value, name } = event.currentTarget;

    if (name === "username" || name === "password") {
      setPayload((current) => ({ ...current, [name]: value }));
    }
  }

  async function login() {
    setLoginError("");

    try {
      const url = `/auth/login?username=${payload.username.trim()}&password=${payload.password.trim()}`;
      const response: ApiResponse = await fetchApi<ApiResponse>(url, {
        method: "POST",
        headers: { "content-Type": "application/json" },
      });

      if (response.code == 401 || response.code == 403) {
        setLoginError(
          response.message
            ? String(response.message)
            : "Invalid login credentials.",
        );
        return;
      }

      if (response.code == 200) {
        const accessToken: string = String(response.data.accessToken);

        localStorage.setItem("TM_DATA", accessToken);
        loadUser(accessToken);
      }
    } catch (error) {
      setLoginError(
        error instanceof Error ? error.message : "Unable to log in.",
      );
    }
  }

  useEffect(() => {
    localStorage.removeItem("TM_DATA");
  }, []);

  useEffect(() => {
    if (!loginError) return;

    const timeoutId = window.setTimeout(() => setLoginError(""), 3000);
    return () => window.clearTimeout(timeoutId);
  }, [loginError]);

  useEffect(() => {
    if (!registrationMessage) return;

    const timeoutId = window.setTimeout(
      () => setRegistrationMessage(""),
      3000,
    );
    return () => window.clearTimeout(timeoutId);
  }, [registrationMessage]);

  return (
    <Container
      className={" h-lvh justify-center py-20   border justify-items-center "}
    >
      <div className="flex flex-col h-full justify-center items-center content-center ">
        {/* <div className=" font-black shadow-2xl shadow-gold  ">TASK MANAGER</div> */}
        <div className="flex justify-center  my-auto">
          <Card className=" w-sm shadow-gold ">
            <CardHeader>
              <CardTitle className="mx-auto ">Login</CardTitle>
            </CardHeader>
            <CardContent>
              {loginError && (
                <Alert variant="destructive" className="mb-4">
                  <AlertTitle>Login failed</AlertTitle>
                  <AlertDescription>{loginError}</AlertDescription>
                </Alert>
              )}
              {registrationMessage && (
                <Alert variant="success" className="mb-4">
                  <AlertTitle>Registration successful</AlertTitle>
                  <AlertDescription>{registrationMessage}</AlertDescription>
                </Alert>
              )}
              <Field>
                <FieldLabel htmlFor="username">email</FieldLabel>
                <Input
                  onChange={handleChange}
                  name="username"
                  id="username"
                  placeholder="example@yahoo.com"
                  type="email"
                ></Input>
              </Field>
              <Field>
                <FieldLabel htmlFor="password">password</FieldLabel>
                <Input
                  id="password"
                  name="password"
                  type="password"
                  onChange={handleChange}
                  minLength={6}
                  placeholder="********"
                />
              </Field>
            </CardContent>
            <CardAction className="flex w-full flex-col items-center gap-2">
              <div className="flex flex-row justify-center gap-5">
                <Button
                  className={"btn shadow-gold"}
                  type="reset"
                  variant={"outline"}
                  size={"sm"}
                >
                  reset
                </Button>
                <Button
                  className={"btn shadow-gold"}
                  variant={"default"}
                  onClick={login}
                  size={"sm"}
                >
                  submit
                </Button>
              </div>
              <div className="flex w-full justify-end">
                <Button
                  type="button"
                  variant="link"
                  size="xs"
                  className="text-amber-500 underline"
                  onClick={() => setRegistrationOpen(true)}
                >
                  Register
                </Button>
              </div>
            </CardAction>
          </Card>
        </div>
      </div>
      <RegistrationForm
        open={registrationOpen}
        onOpenChange={setRegistrationOpen}
        onSuccess={setRegistrationMessage}
      />
    </Container>
  );
}
