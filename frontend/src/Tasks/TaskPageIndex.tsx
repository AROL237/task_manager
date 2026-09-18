import type User from "@/auth/model/User";
import type Tasks from "@/auth/model/Tasks";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Field, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Combobox,
  ComboboxContent,
  ComboboxEmpty,
  ComboboxInput,
  ComboboxItem,
  ComboboxList,
} from "@/components/ui/combobox";
import {
  createTask,
  deleteTask,
  getTasks,
  updateTask,
  type TaskPayload,
} from "@/lib/api";
import {
  ChevronLeftIcon,
  ChevronRightIcon,
  LogOutIcon,
  PlusIcon,
  SearchIcon,
  TrashIcon,
} from "lucide-react";
import { useEffect, useState, type ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";
import TaskCard from "./TaskCard";
import Container from "@/components/ui/custom/Container";

type Props = { user: User; setUser: (user: User | null) => void };
type FilterState = {
  id: string;
  title: string;
  status: "" | "ACTIVE" | "INACTIVE";
  description: string;
};
type FormState = {
  title: string;
  description: string;
  status: "ACTIVE" | "INACTIVE";
};
const initialFilters: FilterState = {
  id: "",
  title: "",
  status: "",
  description: "",
};
const initialForm: FormState = { title: "", description: "", status: "ACTIVE" };
const pageSize = 10;

export default function TaskPageIndex({ user, setUser }: Props) {
  const navigate = useNavigate();
  const [tasks, setTasks] = useState<Tasks[]>([]);
  const [filters, setFilters] = useState(initialFilters);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [form, setForm] = useState(initialForm);
  const [editingTask, setEditingTask] = useState<Tasks | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [deletingTask, setDeletingTask] = useState<Tasks | null>(null);
  const [busy, setBusy] = useState(false);

  const searchValue = JSON.stringify({
    id: filters.id ? Number(filters.id) : null,
    title: filters.title || null,
    description: filters.description || null,
    status: filters.status === "" ? null : filters.status === "ACTIVE",
  });

  async function loadTasks(requestedPage = page, search = searchValue) {
    const result = await getTasks(search, requestedPage, pageSize);
    if (result == null) {
      setUser(null);
      return;
    }
    setTasks(result.content ?? []);
    setPage(result.number ?? requestedPage);
    setTotalPages(result.totalPages ?? 0);
  }

  useEffect(() => {
    void loadTasks(0, "{}");
  }, []);

  const handleChange = (
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    const { name, value } = event.currentTarget;
    setFilters((current) => ({ ...current, [name]: value }));
  };

  const submitForm = async () => {
    if (!form.title.trim()) return;
    setBusy(true);
    const payload: TaskPayload = {
      ...form,
      title: form.title.trim(),
      status: form.status === "ACTIVE",
    };
    const result = editingTask
      ? await updateTask(editingTask.id, payload)
      : await createTask(payload);
    setBusy(false);
    if (result == null) return;
    setFormOpen(false);
    setEditingTask(null);
    setForm(initialForm);
    await loadTasks(page);
  };

  const confirmDelete = async () => {
    if (!deletingTask) return;
    setBusy(true);
    await deleteTask(deletingTask.id);
    setBusy(false);
    setDeletingTask(null);
    await loadTasks(page);
  };

  const openCreate = () => {
    setEditingTask(null);
    setForm(initialForm);
    setFormOpen(true);
  };
  const openEdit = (task: Tasks) => {
    setEditingTask(task);
    setForm({
      title: String(task.title),
      description: String(task.description ?? ""),
      status: task.status ? "ACTIVE" : "INACTIVE",
    });
    setFormOpen(true);
  };
  const applySearch = () => {
    setPage(0);
    void loadTasks(0, searchValue);
  };
  const reset = () => {
    setFilters(initialFilters);
    setPage(0);
    void loadTasks(0, "{}");
  };
  const initials = user.email?.split("@")[0]?.slice(0, 2).toUpperCase() || "U";
  const logout = () => {
    localStorage.removeItem("TM_DATA");
    setUser(null);
    navigate("/auth", { replace: true });
  };

  return (
    <Container className="min-h-lvh border ">
      <header className="flex items-center w-full justify-end gap-3 border-b p-4">
        <span className="hidden md:block">{user.email}</span>
        <Avatar size="lg">
          <AvatarImage alt={String(user.email)} />
          <AvatarFallback className={"font-bold"}>{initials}</AvatarFallback>
        </Avatar>
        <Button variant="outline" onClick={logout}>
          <LogOutIcon />
          Logout
        </Button>
      </header>
      <main className="mx-auto w-full max-w-7xl p-4">
        <div className="flex flex-wrap items-end justify-center gap-3 py-6">
          <Field className="w-40">
            <FieldLabel htmlFor="id">ID</FieldLabel>
            <Input
              id="id"
              name="id"
              value={filters.id}
              onChange={handleChange}
            />
          </Field>
          <Field className="w-48">
            <FieldLabel htmlFor="title">Title</FieldLabel>
            <Input
              id="title"
              name="title"
              value={filters.title}
              onChange={handleChange}
            />
          </Field>
          <Field className="w-36">
            <FieldLabel htmlFor="status">Status</FieldLabel>
            <Combobox
              items={["ACTIVE", "INACTIVE"]}
              value={filters.status || null}
              onValueChange={(value) =>
                setFilters((current) => ({ ...current, status: value ?? "" }))
              }
            >
              <ComboboxInput placeholder="Choose" />
              <ComboboxContent>
                <ComboboxEmpty>No items found.</ComboboxEmpty>
                <ComboboxList>
                  {(item) => (
                    <ComboboxItem key={item} value={item}>
                      {item}
                    </ComboboxItem>
                  )}
                </ComboboxList>
              </ComboboxContent>
            </Combobox>
          </Field>
          <Field className="w-48">
            <FieldLabel htmlFor="description">Description</FieldLabel>
            <Input
              id="description"
              name="description"
              value={filters.description}
              onChange={handleChange}
            />
          </Field>
          <Button onClick={applySearch}>
            <SearchIcon /> Search
          </Button>
          <Button variant="outline" onClick={reset}>
            Reset
          </Button>
        </div>
        <div className="flex justify-end pb-4">
          <Button onClick={openCreate}>
            <PlusIcon /> Create task
          </Button>
        </div>
        <div className="flex flex-wrap justify-center gap-3">
          {tasks.map((task) => (
            <div className="w-full sm:w-80" key={task.id}>
              <TaskCard
                {...task}
                onEdit={() => openEdit(task)}
                onDelete={() => setDeletingTask(task)}
              />
            </div>
          ))}
        </div>
        <div className="flex items-center justify-center gap-4 py-8">
          <Button
            variant="outline"
            size="icon"
            disabled={page === 0}
            onClick={() => void loadTasks(page - 1)}
          >
            <ChevronLeftIcon />
          </Button>
          <span>
            Page {totalPages === 0 ? 0 : page + 1} of {totalPages}
          </span>
          <Button
            variant="outline"
            size="icon"
            disabled={page + 1 >= totalPages}
            onClick={() => void loadTasks(page + 1)}
          >
            <ChevronRightIcon />
          </Button>
        </div>
      </main>

      <Dialog open={formOpen} onOpenChange={setFormOpen}>
        <DialogContent>
          <DialogClose onClick={() => setFormOpen(false)} />
          <DialogHeader>
            <DialogTitle>
              {editingTask ? "Update task" : "Create task"}
            </DialogTitle>
            <DialogDescription>Enter the task details below.</DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <Field>
              <FieldLabel htmlFor="task-title">Title</FieldLabel>
              <Input
                id="task-title"
                value={form.title}
                onChange={(event) =>
                  setForm((current) => ({
                    ...current,
                    title: event.target.value,
                  }))
                }
              />
            </Field>
            <Field>
              <FieldLabel htmlFor="task-description">Description</FieldLabel>
              <Textarea
                id="task-description"
                value={form.description}
                onChange={(event) =>
                  setForm((current) => ({
                    ...current,
                    description: event.target.value,
                  }))
                }
              />
            </Field>
            <Field>
              <FieldLabel htmlFor="task-status">Status</FieldLabel>
              <select
                id="task-status"
                className="h-9 rounded-md border bg-transparent px-3"
                value={form.status}
                onChange={(event) =>
                  setForm((current) => ({
                    ...current,
                    status: event.target.value as FormState["status"],
                  }))
                }
              >
                <option>ACTIVE</option>
                <option>INACTIVE</option>
              </select>
            </Field>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setFormOpen(false)}>
                Cancel
              </Button>
              <Button onClick={() => void submitForm()} disabled={busy}>
                {editingTask ? "Update" : "Create"}
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
      <Dialog
        open={deletingTask != null}
        onOpenChange={(open) => !open && setDeletingTask(null)}
      >
        <DialogContent>
          <DialogClose onClick={() => setDeletingTask(null)} />
          <DialogHeader>
            <DialogTitle>Delete task?</DialogTitle>
            <DialogDescription>This action cannot be undone.</DialogDescription>
          </DialogHeader>
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => setDeletingTask(null)}>
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={() => void confirmDelete()}
              disabled={busy}
            >
              <TrashIcon /> Delete
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </Container>
  );
}
