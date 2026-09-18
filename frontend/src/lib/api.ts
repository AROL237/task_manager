import type ApiResponse from "@/auth/model/ApiResponse";
import type User from "@/auth/model/User";
import type Tasks from "@/auth/model/Tasks";

const BASE_URL = import.meta.env.VITE_BASE_URL ?? "/api";
let TOKEN = null;

export async function fetchApi<T>(
  urlPath: string,
  options?: RequestInit,
): Promise<T> {
  const response = await fetch(BASE_URL + urlPath, options);

  if (!response.ok) {
    throw new Error(
      `Request failed: ${response.status} ${response.statusText}`,
    );
  }

  return (await response.json()) as T;
}

export async function getUser(token: String): Promise<User | null> {
  const url = `/auth/profile`;

  const res = await fetchApi<ApiResponse>(url, {
    method: "GET",
    headers: {
      "content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });

  if (res.code == 200 && res.success == true) {
    const user: User = res.data;
    return user;
  } else {
    return null;
  }
}

export async function getTasks(
  search = "{}",
  page = 0,
  size = 10,
): Promise<any> {
  const params = new URLSearchParams({
    search,
    page: String(page),
    size: String(size),
  });
  const url = `/tasks?${params.toString()}`;

  TOKEN = getAccessToken();

  if (TOKEN == null) {
    return null;
  }

  const res = await fetchApi<ApiResponse>(url, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${TOKEN}`,
    },
  });

  if (res.code == 200) return res.data;
  else if (res.code == 401 || res.code == 403) return null;
  else return res;
}

export type TaskPayload = {
  title: string;
  description: string;
  status: boolean;
};

async function taskMutation(url: string, method: string, body?: unknown) {
  const token = getAccessToken();
  if (token == null) return null;

  const response = await fetchApi<ApiResponse>(url, {
    method,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: body == null ? undefined : JSON.stringify(body),
  });

  return response.code === 200 ? response.data : null;
}

export function createTask(payload: TaskPayload) {
  return taskMutation("/tasks", "POST", payload);
}

export function updateTask(id: Tasks["id"], payload: TaskPayload) {
  return taskMutation(`/tasks/${id}`, "PUT", payload);
}

export function deleteTask(id: Tasks["id"]) {
  return taskMutation(`/tasks/${id}`, "DELETE");
}

export function getAccessToken() {
  let str = localStorage.getItem("TM_DATA");

  if (str != null) return str;
  else return null;
}
