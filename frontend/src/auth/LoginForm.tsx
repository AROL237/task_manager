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

import { useEffect, useState, type ChangeEvent } from "react";
import { fetchApi } from "@/lib/api";
import type ApiResponse from "./model/ApiResponse";
import type LoginPayload from "./model/LoginPayload";

type LoginPageProps = {
  loadUser: any;
};

export default function LoginPage({ loadUser }: LoginPageProps) {
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
    const url = `/auth/login?username=${payload.username.trim()}&password=${payload.password.trim()}`;
    const response: ApiResponse = await fetchApi<ApiResponse>(url, {
      method: "POST",
      headers: { "content-Type": "application/json" },
    });

    if (response.code == 200) {
      let accessToken: String = response.data.accessToken;

      localStorage.setItem("TM_DATA", accessToken.toString());
      loadUser(accessToken);

      return;
    }
  }

  useEffect(() => {
    localStorage.removeItem("TM_DATA");
  }, []);

  useEffect(() => {}, []);

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
            <CardAction className=" flex flex-row w-full justify-center gap-5">
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
            </CardAction>
          </Card>
        </div>
      </div>
    </Container>
  );
}
