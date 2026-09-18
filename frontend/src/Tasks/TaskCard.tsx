import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { EditIcon, TrashIcon } from "lucide-react";

type TaskCardProps = {
  id?: number;
  title?: string;
  description?: string;
  status?: boolean;
  onEdit: () => void;
  onDelete: () => void;
};

export default function TaskCard({
  id,
  title,
  status,
  description,
  onEdit,
  onDelete,
}: TaskCardProps) {
  const statuStyle = status ? "bg-green-400 " : "bg-red-500";
  return (
    <Card className="w-full   shadow">
      <CardHeader>
        <div className="w-full flex flex-row mx-auto justify-between content-between  ">
          <CardTitle className="flex  justify-ends content-end">
            {`# ` + id}
          </CardTitle>
          <Badge variant={"default"} className={`${statuStyle} `}>
            {status ? "Active" : "Inactive"}
          </Badge>
        </div>
        <CardTitle className="font-bold text-2xl">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <CardDescription className="line-clamp-3">
          {description}
        </CardDescription>
      </CardContent>
      <CardAction className="flex justify-end w-full gap-3 px-5">
        <Button
          variant={"outline"}
          className={"bg-blue-400"}
          size={"icon"}
          onClick={onEdit}
        >
          <EditIcon />
        </Button>
        <Button variant={"destructive"} size={"icon"} onClick={onDelete}>
          <TrashIcon />
        </Button>
      </CardAction>
    </Card>
  );
}
